package com.scube.document.storage.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("aws.s3")
public class S3Properties {
    private String bucket;
    private String storagePath;
    private String archivePath;
}