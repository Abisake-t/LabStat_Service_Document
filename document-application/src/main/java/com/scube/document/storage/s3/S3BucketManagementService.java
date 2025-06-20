package com.scube.document.storage.s3;

import com.scube.document.storage.conditionals.IsS3StorageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Conditional(IsS3StorageType.class)
public class S3BucketManagementService implements IS3BucketManagementService {
    private final S3Client s3Client;

    public void cloneBucket(String sourceBucket, String targetBucket) {
        createBucket(targetBucket);
        copyBucketSettings(sourceBucket, targetBucket);
    }

    public void createBucket(String bucketName) {
        s3Client.createBucket(bucketRequest -> bucketRequest
                .bucket(bucketName)
                .acl(BucketCannedACL.PRIVATE)
                .build());
    }

    public List<String> listBuckets(Predicate<String> nameFilter) {
        return s3Client.listBuckets().buckets().stream()
                .map(Bucket::name)
                .filter(nameFilter)
                .toList();
    }

    public List<String> listBuckets() {
        return streamBuckets(s3Client).toList();
    }

    public void copyBucketSettings(String sourceBucket, String targetBucket) {
        copyBucketSettings(s3Client, sourceBucket, targetBucket);
    }

    private static Stream<String> streamBuckets(S3Client s3) {
        return s3.listBuckets().buckets().stream().map(Bucket::name);
    }

    private static void copyBucketSettings(S3Client s3, String sourceBucket, String targetBucket) {
        copyBucketPolicy(s3, sourceBucket, targetBucket);
        copyCorsConfiguration(s3, sourceBucket, targetBucket);
        copyBucketTags(s3, sourceBucket, targetBucket);
        copyLifecycleConfiguration(s3, sourceBucket, targetBucket);
        copyLoggingConfiguration(s3, sourceBucket, targetBucket);
        copyVersioningConfiguration(s3, sourceBucket, targetBucket);
        copyWebsiteConfiguration(s3, sourceBucket, targetBucket);
    }

    private static void copyBucketPolicy(S3Client s3, String sourceBucket, String targetBucket) {
        try {
            String policy = s3.getBucketPolicy(r -> r.bucket(sourceBucket)).policy();
            s3.putBucketPolicy(r -> r.bucket(targetBucket).policy(policy));
        } catch (S3Exception e) {
            if (!e.awsErrorDetails().errorCode().equals("NoSuchBucketPolicy")) {
                throw e;
            }
        }
    }

    private static void copyCorsConfiguration(S3Client s3, String sourceBucket, String targetBucket) {
        try {
            GetBucketCorsResponse corsResponse = s3.getBucketCors(r -> r.bucket(sourceBucket));

            s3.putBucketCors(r -> r.bucket(targetBucket).corsConfiguration(a ->
                    a.corsRules(corsResponse.corsRules())));
        } catch (S3Exception e) {
            if (!e.awsErrorDetails().errorCode().equals("NoSuchCORSConfiguration")) {
                throw e;
            }
        }
    }

    private static void copyBucketTags(S3Client s3, String sourceBucket, String targetBucket) {
        try {
            GetBucketTaggingResponse taggingResponse = s3.getBucketTagging(r -> r.bucket(sourceBucket));
            s3.putBucketTagging(r -> r.bucket(targetBucket).tagging(t -> t.tagSet(taggingResponse.tagSet())));
        } catch (S3Exception e) {
            if (!e.awsErrorDetails().errorCode().equals("NoSuchTagSet")) {
                throw e;
            }
        }
    }

    private static void copyLifecycleConfiguration(S3Client s3, String sourceBucket, String targetBucket) {
        try {
            GetBucketLifecycleConfigurationResponse lifecycleResponse = s3.getBucketLifecycleConfiguration(r -> r.bucket(sourceBucket));
            s3.putBucketLifecycleConfiguration(r -> r.bucket(targetBucket).lifecycleConfiguration(l -> l.rules(lifecycleResponse.rules())));
        } catch (S3Exception e) {
            if (!e.awsErrorDetails().errorCode().equals("NoSuchLifecycleConfiguration")) {
                throw e;
            }
        }
    }

    private static void copyLoggingConfiguration(S3Client s3, String sourceBucket, String targetBucket) {
        GetBucketLoggingResponse loggingResponse = s3.getBucketLogging(r -> r.bucket(sourceBucket));
        if (loggingResponse.loggingEnabled() != null) {
            s3.putBucketLogging(r -> r.bucket(targetBucket)
                    .bucketLoggingStatus(b -> b.loggingEnabled(loggingResponse.loggingEnabled())));
        }
    }

    private static void copyVersioningConfiguration(S3Client s3, String sourceBucket, String targetBucket) {
        GetBucketVersioningResponse versioningResponse = s3.getBucketVersioning(r -> r.bucket(sourceBucket));
        if (versioningResponse.status() != null) {
            s3.putBucketVersioning(r -> r.bucket(targetBucket)
                    .versioningConfiguration(v -> v.status(versioningResponse.status())));
        }
    }

    private static void copyWebsiteConfiguration(S3Client s3, String sourceBucket, String targetBucket) {
        try {
            GetBucketWebsiteResponse websiteResponse = s3.getBucketWebsite(r -> r.bucket(sourceBucket));
            s3.putBucketWebsite(r -> r.bucket(targetBucket)
                    .websiteConfiguration(w -> w
                            .indexDocument(d -> d.suffix(websiteResponse.indexDocument().suffix()))
                            .errorDocument(d -> d.key(websiteResponse.errorDocument().key()))
                            .routingRules(websiteResponse.routingRules())
                            .redirectAllRequestsTo(websiteResponse.redirectAllRequestsTo())
                    )
            );
        } catch (S3Exception e) {
            if (!e.awsErrorDetails().errorCode().equals("NoSuchWebsiteConfiguration")) {
                throw e;
            }
        }
    }
}
