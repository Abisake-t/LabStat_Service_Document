package com.scube.document.service;

import com.scube.document.dto.FileUploadRequestDTO;
import com.scube.document.dto.FileUploadResponseDTO;
import com.scube.document.model.Document;
import com.scube.document.repository.DocumentRepository;
import com.scube.document.storage.IFileStorageService;
import com.scube.lib.antivirus.IAntivirusClient;
import com.scube.rabbit.core.AmqpGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceImplTest {
    @Mock
    private IFileStorageService fileStorageService;

    @Mock
    private DocumentRepository documentRepository;

    private DocumentService documentService;

    @Mock
    private IAntivirusClient antivirusClient;

    @Mock
    private AmqpGateway amqpGateway;

    @BeforeEach
    void setup() {
        documentService = new DocumentServiceImpl(fileStorageService, documentRepository, antivirusClient, amqpGateway);
    }

    @Test
    void testUpload() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "Hello, World!".getBytes());
        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .file(file).build();
        UUID randomUUID = UUID.randomUUID();
        String downloadUrl = "./tmp/test.txt";

        Document document = Document.builder()
                .id(1L)
                .uuid(randomUUID)
                .documentUrl(Paths.get(downloadUrl).toString())
                .size(file.getSize())
                .contentType(file.getContentType())
                .name(file.getOriginalFilename())
                .build();

        FileUploadResponseDTO expectedResponseDTO = FileUploadResponseDTO.builder()
                .documentUUID(document.getUuid())
                .documentUrl(Paths.get(downloadUrl).toString()).build();

        Mockito.when(documentRepository.save(any(Document.class))).thenReturn(document);
        Mockito.when(fileStorageService.save(fileUploadRequestDTO.getFile(), document)).thenReturn(document);
        Mockito.when(antivirusClient.scan(any(MultipartFile.class))).thenReturn(true);

        FileUploadResponseDTO actualResponseDTO = documentService.upload(fileUploadRequestDTO.getFile(), null, null);

        assertThat(actualResponseDTO)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponseDTO);
    }

    @Test
    void testDownload() {
        UUID documentId = UUID.randomUUID();
        Document document = Document.builder()
                .uuid(documentId)
                .size(12L)
                .contentType("text/plain")
                .name("test.txt")
                .build();

        byte[] bytes = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        Mockito.when(documentRepository.findByUuidOrThrow(documentId)).thenReturn(document);
        Mockito.when(fileStorageService.load(document)).thenReturn(resource);

        Resource actualResource = documentService.download(document.getUuid(), null).getResource();

        assertEquals(resource, actualResource);
    }
}
