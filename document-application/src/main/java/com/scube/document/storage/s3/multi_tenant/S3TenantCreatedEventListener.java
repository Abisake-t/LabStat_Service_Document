package com.scube.document.storage.s3.multi_tenant;

import com.scube.document.storage.conditionals.IsS3StorageType;
import com.scube.document.storage.s3.IS3BucketManagementService;
import com.scube.multi.tenant.management.event.TenantCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(IsS3StorageType.class)
public class S3TenantCreatedEventListener implements ApplicationListener<TenantCreatedEvent> {
    public static final String CLERKXPRESS_TEMPLATE_BUCKET = "clerkxpress-template-bucket";
    private final IS3BucketManagementService s3BucketManagementService;

    @Override
    public void onApplicationEvent(TenantCreatedEvent event) {
        log.info("Creating S3 bucket for tenant {}", event.getTenantIdentifier());

        String bucketName = "clerkxpress-" + event.getTenantIdentifier() + "-bucket";
        s3BucketManagementService.cloneBucket(CLERKXPRESS_TEMPLATE_BUCKET, bucketName);
    }
}

