package com.scube.document.service;

import com.scube.document.dto.DocumentDto;
import com.scube.document.dto.DownloadResult;
import com.scube.document.dto.FileUpdateResponse;
import com.scube.document.dto.FileUploadResponseDTO;
import com.scube.document.mapper.DocumentMapper;
import com.scube.document.mapper.DocumentMapperImpl;
import com.scube.document.model.Document;
import com.scube.document.model.DocumentHistory;
import com.scube.document.rabbit.dto.VirusDetectedEvent;
import com.scube.document.repository.DocumentRepository;
import com.scube.document.storage.IFileStorageService;
import com.scube.lib.antivirus.IAntivirusClient;
import com.scube.rabbit.core.AmqpGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private final IFileStorageService fileStorageService;
    private final DocumentRepository documentRepository;
    private final IAntivirusClient antivirusClient;
    private final AmqpGateway amqpGateway;
    private final DocumentMapper documentMapper = new DocumentMapperImpl();

    /**
     * Stores a file and persists the file metadata in
     * the database
     *
     * @param uploadFile
     * @param path
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public FileUploadResponseDTO upload(MultipartFile uploadFile, String path, Boolean storedWithOriginalFilename) {
        log.debug("DocumentServiceImpl.upload()");

        scanForViruses(uploadFile);

        // 2. Store the file with null URL, so we can generate a UUID
        Document document = Document.builder()
                .size(uploadFile.getSize())
                .contentType(uploadFile.getContentType())
                .name(uploadFile.getOriginalFilename())
                .path(path)
                .storedWithOriginalName(storedWithOriginalFilename)
                .versionNumber(1)
                .build();

        log.debug("Saving the file metadata to the db.");
        Document savedDocument = documentRepository.save(document);
        log.debug("Saved document: " + savedDocument);

        // 3. Store the file
        log.debug("Storing the file.");
        fileStorageService.save(uploadFile, savedDocument);

        // 4. Update Database table with the URL
        log.debug("Updating the db url.");
        documentRepository.save(savedDocument);

        // 5. Populate the Response
        log.debug("Populating response.");
        FileUploadResponseDTO responseDTO = FileUploadResponseDTO.builder()
                .documentUUID(savedDocument.getUuid())
                .documentUrl(savedDocument.getDocumentUrl())
                .build();

        log.debug("fileUploadResponseDTO" + responseDTO);

        // 6. return responseDTO.
        return responseDTO;
    }

    @Override
    @Transactional
    public FileUpdateResponse update(UUID uuid, MultipartFile updatedFile) {
        log.debug("DocumentServiceImpl.version()");

        scanForViruses(updatedFile);

        Document document = documentRepository.findByUuidOrThrow(uuid);

        document.addHistory();
        document.setSize(updatedFile.getSize());
        document.setContentType(updatedFile.getContentType());
        document.setName(updatedFile.getOriginalFilename());
        document.setVersionNumber(document.getVersionNumber() + 1);

        fileStorageService.save(updatedFile, document);

        documentRepository.save(document);

        return new FileUpdateResponse(document.getUuid(), document.getVersionNumber());
    }

    @Override
    @Transactional
    public DownloadResult download(UUID documentUUID, Integer version) {
        log.debug("DocumentServiceImpl.download()");

        //TODO Need to change this to append tenantID to path for retrieval
        Document document = documentRepository.findByUuidOrThrow(documentUUID);

        validateVersion(version, document);

        DocumentDto dto = documentMapper.toDto(document);

        if (version != null && version != document.getVersionNumber()) {
                var documentVersion = getByVersionId(version, document);

                dto = documentMapper.toDocumentDto(documentVersion);
                document.setVersionId(documentVersion.getVersionId());
        }

        return new DownloadResult(fileStorageService.load(document), dto);
    }

    private static DocumentHistory getByVersionId(Integer version, Document document) {
        return document.getDocumentHistory().stream()
                .filter(history -> history.getVersionNumber().equals(version))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document version not found."));
    }

    private static void validateVersion(Integer version, Document document) {
        if (version != null
                && version != document.getVersionNumber()
                && !document.getDocumentHistory().stream().anyMatch(history -> history.getVersionNumber().equals(version))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File with version: " + version + " not found");
        }
    }

    @Override
    @Transactional
    public Boolean delete(UUID documentUUID) {
        log.debug("DocumentServiceImpl.delete()");

        Optional<Document> document = documentRepository.findByUuid(documentUUID);

        if (document.isPresent()) {
            log.debug("Document deleted.");
            fileStorageService.delete(document.get());
            documentRepository.deleteById(document.get().getId());
            return true;
        }

        log.debug("Document not found. Nothing deleted.");
        return false;
    }

    @Override
    public List<Document> getMetadata(List<UUID> documentUUIDs) {
        log.debug("DocumentServiceImpl.getDocuments()");
        return documentRepository.findByUuidIn(documentUUIDs);
    }

    @Override
    public List<DocumentDto> getHistory(UUID uuid) {
        Document document = documentRepository.findByUuidOrThrow(uuid);

        List<DocumentDto> documents = new ArrayList<>();
        documents.add(documentMapper.toDto(document));
        documents.addAll(documentMapper.toDocumentDto(document.getDocumentHistory()));

        return documents;
    }

    private void scanForViruses(MultipartFile file) {
        boolean isSafe = antivirusClient.scan(file);

        if (!isSafe) {
            amqpGateway.publish(new VirusDetectedEvent(file.getOriginalFilename()));
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "File is unsafe");
        }
    }

    public ZipOutputStream writeZipFile(Set<UUID> documentUuids, ZipOutputStream zipStream) throws IOException {
        for (UUID docUuid : documentUuids) {
            var result = download(docUuid, null);

            zipStream.putNextEntry(new ZipEntry(result.getDocument().getName()));

            try (InputStream in = result.getResource().getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    zipStream.write(buffer, 0, bytesRead);
                }
            }

            zipStream.closeEntry();
        }

        return zipStream;
    }
}
