package com.scube.document.storage.s3;

import com.scube.config_utils.app_property.AppPropertyValue;
import com.scube.document.model.Document;
import com.scube.document.storage.IFileStorageService;
import com.scube.document.storage.conditionals.IsS3StorageType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
@Conditional(IsS3StorageType.class)
public class S3FileStorageService implements IFileStorageService {
    private final S3Client s3Client;
    private final String s3Environment;

    @AppPropertyValue
    @Setter
    private S3Properties s3Properties;

    @Override
    @SneakyThrows
    public Document save(MultipartFile file, Document document) {
        log.debug("S3FileStorageService.save()");

        if (isEmpty(document.getPath())) {
            document.setPath(s3Properties.getStoragePath());
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(getS3Key(document))
                .contentType(document.getContentType())
                .build();

        PutObjectResponse putObjectResponse =
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        document.setVersionId(putObjectResponse.versionId());
        document.setDocumentUrl(s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(s3Properties.getBucket()).key(getS3Key(document)).build()).toString());

        String eTag = putObjectResponse.eTag() == null ? "" : putObjectResponse.eTag();

        String md5Hash = eTag.replace("\"", "");

        document.setMd5Hash(md5Hash);

        return document;
    }

    @Override
    public Resource load(Document document) {
        log.debug("S3FileStorageService.load()");

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(getS3Key(document))
                .build();

        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(getObjectRequest);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s3Object.asByteArray());

        return new InputStreamResource(inputStream);
    }

    public void delete(Document document) {
        log.debug("S3FileStorageService.delete()");

        // Delete the original object
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(getS3Key(document))
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private final String getS3Key(Document document) {
        String documentName = document.getName();

        if (document.getStoredWithOriginalName() == null
            || document.getStoredWithOriginalName() == false) {
            documentName = document.getUuid().toString();
        }

        return s3Environment + "/" + document.getPath() + documentName;
    }
}