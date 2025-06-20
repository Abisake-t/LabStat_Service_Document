package com.scube.document.controller;

import com.scube.client.ServiceUrlConstant;
import com.scube.client.annotation.GenerateHttpExchange;
import com.scube.document.dto.DocumentDto;
import com.scube.document.dto.DownloadResult;
import com.scube.document.mapper.DocumentMapper;
import com.scube.document.permission.Permissions;
import com.scube.document.service.DocumentService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@GenerateHttpExchange(value = ServiceUrlConstant.DOCUMENT_SERVICE)
public class LoggedInUserDocumentController {
    private final DocumentService documentService;
    private final DocumentMapper mapper;

    @GetMapping(value = "/preview")
    @RolesAllowed(Permissions.LoggedInUserDocument.GET_FILE_PREVIEW)
    public ResponseEntity<byte[]> getFilePreview(@RequestParam UUID documentUUID, @RequestParam(required = false) Integer version) throws IOException {
        DownloadResult downloadResult = documentService.download(documentUUID, version);

        DocumentDto metadata = downloadResult.getDocument();

        Resource file = downloadResult.getResource();

        InputStream inputStream = file.getInputStream();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(inputStream.readAllBytes());
    }

    @GetMapping("/download")
    @RolesAllowed(Permissions.LoggedInUserDocument.GET_FILE)
    public ResponseEntity<Resource> getFile(@RequestParam UUID documentUUID, @RequestParam(required = false) Integer version) {
        log.debug("DocumentController.getFile()");
        log.debug("documentUUID: " + documentUUID);

        DownloadResult downloadResult = documentService.download(documentUUID, version);

        DocumentDto metadata = downloadResult.getDocument();

        Resource file = downloadResult.getResource();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getName() + "\"")
                .body(file);
    }

    @GetMapping("/metadata")
    @RolesAllowed(Permissions.LoggedInUserDocument.GET_METADATA)
    public List<DocumentDto> getMetadata(@RequestParam List<UUID> documentUUIDs) {
        log.debug("DocumentController.getMetadata()");
        log.debug("documentUUIDs: " + documentUUIDs);

        return mapper.toDto(documentService.getMetadata(documentUUIDs));
    }

    @GetMapping("{documentUuid}/history")
    @RolesAllowed(Permissions.LoggedInUserDocument.GET_HISTORY)
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentDto> getHistory(@PathVariable UUID documentUuid) {
        log.debug("DocumentController.getHistory()");

        return documentService.getHistory(documentUuid);
    }
}
