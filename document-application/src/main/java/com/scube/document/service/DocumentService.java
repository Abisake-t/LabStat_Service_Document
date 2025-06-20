package com.scube.document.service;

import com.scube.document.dto.DocumentDto;
import com.scube.document.dto.DownloadResult;
import com.scube.document.dto.FileUpdateResponse;
import com.scube.document.dto.FileUploadResponseDTO;
import com.scube.document.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

public interface DocumentService {

    /**
     *
     * @param file
     * @param path
     * @return
     */
    FileUploadResponseDTO upload(MultipartFile file, String path, Boolean storedWithOriginalName);

    /**
     *
     * @param uuid
     * @param file
     * @return FileUpdateResponse
     */
    FileUpdateResponse update(UUID uuid, MultipartFile file);

    /**
     * @param documentUUID
     * @return Resource
     */
    DownloadResult download(UUID documentUUID, Integer version);

    /**
     * @param documentUUID
     * @return Boolean
     */
    Boolean delete(UUID documentUUID);

    /**
     * @param documentUUIDs
     * @return List<Document>
     */
    List<Document> getMetadata(List<UUID> documentUUIDs);

    /**
     *
     * @param uuid
     * @return
     */
    List<DocumentDto> getHistory(UUID uuid);

    /**
     *
     * @param documentUuids
     * @param zipStream
     * @return
     * @throws IOException
     */
    ZipOutputStream writeZipFile(Set<UUID> documentUuids, ZipOutputStream zipStream) throws IOException;
}
