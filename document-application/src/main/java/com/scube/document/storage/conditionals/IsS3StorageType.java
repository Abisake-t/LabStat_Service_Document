package com.scube.document.storage.conditionals;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class IsS3StorageType extends AllNestedConditions {
    public IsS3StorageType() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "aws.storage-type", havingValue = "s3")
    static class IsEnabled {
    }
}