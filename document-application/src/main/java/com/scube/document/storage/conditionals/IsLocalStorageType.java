package com.scube.document.storage.conditionals;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class IsLocalStorageType extends AllNestedConditions {
    public IsLocalStorageType() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "aws.storage-type", havingValue = "local", matchIfMissing = true)
    static class IsEnabled {
    }
}