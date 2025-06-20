package com.scube.document.service;

import com.scube.config_utils.app_property.WithAppProperty;
import com.scube.config_utils.app_property.field_injection.AppPropertyResolver;
import com.scube.document.model.Document;
import com.scube.document.storage.s3.S3FileStorageService;
import com.scube.document.storage.s3.S3Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WithAppProperty({"aws.s3.bucket=test-bucket", "aws.s3.storage-path=storage-path/", "aws.s3.archive-path=archive-path/"})
class S3FileStorageServiceTest {

    @Mock
    S3Client s3Client;

    @InjectMocks
    S3FileStorageService s3FileStorageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        s3FileStorageService = new S3FileStorageService(s3Client, "test");
        s3FileStorageService.setS3Properties(AppPropertyResolver.resolve(S3Properties.class));
    }

    @Test
    public void save() throws Exception {
        String filename = "test.txt";
        MockMultipartFile multipartFile = new MockMultipartFile("file", filename, "text/plain", "test data".getBytes());

        String expectedUrl = "http://example.com/fakepath";

        S3Utilities s3Utilities = mock(S3Utilities.class);

        Document document = new Document();
        document.setUuid(UUID.randomUUID());
        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn(new URL(expectedUrl));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().versionId("1").build());

        Document savedDocument = s3FileStorageService.save(multipartFile, document);

        assertEquals("1", savedDocument.getVersionId());
        assertEquals(expectedUrl, savedDocument.getDocumentUrl());
    }

    @Test
    public void load() throws IOException {
        String fileName = "test.txt";
        byte[] expectedFileContents = "test".getBytes();

        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        ByteArrayInputStream stream = new ByteArrayInputStream(expectedFileContents);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(ResponseBytes.fromByteArray(getObjectResponse, stream.readAllBytes()));

        Document document = new Document();
        document.setName(fileName);
        document.setUuid(UUID.randomUUID());

        Resource resource = s3FileStorageService.load(document);

        assertTrue(Arrays.equals(resource.getInputStream().readAllBytes(), expectedFileContents));
    }

    @Test
    public void delete() {
        Document document = new Document();
        document.setName("test.txt");
        document.setUuid(UUID.randomUUID());

        s3FileStorageService.delete(document);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
