package com.scube.document.storage;

import com.scube.document.model.Document;
import com.scube.document.storage.conditionals.IsLocalStorageType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service(value = "LocalFileStorage")
@Conditional(IsLocalStorageType.class)
public class LocalFileStorage implements IFileStorageService {

    private final Path root = Paths.get("uploads");

    @SneakyThrows
    public void init() {
        log.debug("LocalFileStorage.init()");

        Files.createDirectories(root);
    }

    @Override
    @SneakyThrows
    public Document save(MultipartFile file, Document document) {
        log.debug("LocalFileStorage.save()");

        if (!Files.exists(root)) init();
        Path newPath = this.root.resolve(Objects.requireNonNull(document.getUuid().toString()));
        Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
        document.setDocumentUrl(newPath.toString());
        document.setVersionId(UUID.randomUUID().toString());
        return document;
    }

    @Override
    @SneakyThrows
    public Resource load(Document document) {
        log.debug("LocalFileStorage.load()");

        Path file;
        if (Boolean.TRUE.equals(document.getStoredWithOriginalName())) {
            file = this.root.resolve(document.getName());
        } else {
            file = this.root.resolve(document.getUuid().toString());
        }
        return new UrlResource(file.toUri());
    }

    @Override
    public void delete(Document document) {
        log.debug("LocalFileStorage.delete()");
        log.debug("Local file storage delete is not implemented");
    }

    public void deleteAll() {
        log.debug("LocalFileStorage.deleteAll()");
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @SneakyThrows
    public Stream<Path> loadAll() {
        log.debug("LocalFileStorage.loadAll()");

        return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
    }
}
