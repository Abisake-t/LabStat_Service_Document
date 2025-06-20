package com.scube.document.storage.s3.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3EnvironmentConfig {
    private final String s3Environment;

    public S3EnvironmentConfig(ApplicationContext context) {
        this.s3Environment = context.getEnvironment().getActiveProfiles()[0];
    }

    @Bean
    public String s3Environment() {
        return s3Environment;
    }
}
