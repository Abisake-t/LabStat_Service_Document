package com.scube.document.storage;

import com.scube.document.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for local File Storage
 */
public interface IFileStorageService {
    /**
     * Stores the file and adds the file URL to the document object snd returns it
     *
     * @param file
     * @return Document
     */

    Document save(MultipartFile file, Document document);

    /**
     * @param document
     * @return Resource of the file
     */

    Resource load(Document document);

    /**
     *
     * @param document
     */
    void delete(Document document);
}
