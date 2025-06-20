package com.scube.document.controller;

import com.scube.client.ServiceUrlConstant;
import com.scube.client.annotation.GenerateHttpExchange;
import com.scube.document.dto.*;
import com.scube.document.mapper.DocumentMapper;
import com.scube.document.permission.Permissions;
import com.scube.document.service.DocumentService;
import com.scube.lib.misc.annotations.validation.NoValidation;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@GenerateHttpExchange(value = ServiceUrlConstant.DOCUMENT_SERVICE)
@Validated
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentMapper mapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed(Permissions.Document.HANDLE_FILE_UPLOAD)
    public ResponseEntity<FileUploadResponseDTO> handleFileUpload(@ModelAttribute @RequestBody FileUploadRequestDTO requestDTO,
                                                                  @NoValidation @RequestParam(required = false) String path,
                                                                  @RequestParam(required = false) Boolean storedWithOriginalName) {
        log.debug("DocumentController.handleFileUpload()");

        return ResponseEntity.ok(documentService.upload(requestDTO.getFile(), path, storedWithOriginalName));
    }

    @PostMapping(value = "/{uuid}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed(Permissions.Document.HANDLE_FILE_UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public FileUpdateResponse handleFileUpdate(@PathVariable UUID uuid, @ModelAttribute @RequestBody FileUpdateRequest request) {
        log.debug("DocumentController.handleFileUpdate()");

        return documentService.update(uuid, request.getFile());
    }

    @GetMapping("/download")
    @RolesAllowed(Permissions.Document.GET_FILE)
    public ResponseEntity<Resource> getFile(@RequestParam UUID documentUUID,
                                            @RequestParam(required = false) Integer version,
                                            @RequestParam(required = false, defaultValue = "false") Boolean preview) {
        log.debug("DocumentController.getFile()");

        DownloadResult downloadResult = documentService.download(documentUUID, version);

        DocumentDto metadata = downloadResult.getDocument();

        Resource file = downloadResult.getResource();

        String contentDisposition = Boolean.TRUE.equals(preview) ? "inline" : "attachment";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("%s; filename=\"%s\"", contentDisposition, metadata.getName()))
                .body(file);
    }

    @GetMapping("/metadata")
    @RolesAllowed(Permissions.Document.GET_METADATA)
    public List<DocumentDto> getMetadata(@RequestParam List<UUID> documentUUIDs) {
        log.debug("DocumentController.getMetadata()");

        return mapper.toDto(documentService.getMetadata(documentUUIDs));
    }

    @DeleteMapping("{uuid}")
    @RolesAllowed(Permissions.Document.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        log.debug("DocumentController.delete()");
        log.debug("documentUUID: " + uuid);

        if (documentService.delete(uuid)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{documentUuid}/history")
    @RolesAllowed(Permissions.Document.GET_HISTORY)
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentDto> getHistory(@PathVariable UUID documentUuid) {
        log.debug("DocumentController.getHistory()");

        return documentService.getHistory(documentUuid);
    }

    @GetMapping("/downloadZip")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed(Permissions.Document.DOWNLOAD_ZIP)
    public ResponseEntity<Resource> downloadZip(@RequestParam Set<UUID> documentUuids,
                                                @RequestParam(required = false) @NoValidation String zipFileName) throws IOException {
        String fileName = (zipFileName != null && !zipFileName.isEmpty())
                ? zipFileName
                : "files.zip";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipStream = new ZipOutputStream(baos)) {
            documentService.writeZipFile(documentUuids, zipStream);
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
