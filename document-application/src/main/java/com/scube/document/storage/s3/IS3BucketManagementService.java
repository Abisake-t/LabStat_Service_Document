package com.scube.document.storage.s3;

import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for managing S3 buckets, providing operations for creation, cloning, listing, and copying settings.
 */
public interface IS3BucketManagementService {

    /**
     * Clones an existing S3 bucket to a new bucket, copying all contents and settings.
     *
     * @param sourceBucket The name of the source bucket to clone from.
     * @param targetBucket The name of the target bucket to clone to.
     * @throws IllegalArgumentException if either bucket name is invalid.
     * @throws S3Exception if there's an error accessing or modifying the buckets.
     */
    void cloneBucket(String sourceBucket, String targetBucket);

    /**
     * Creates a new S3 bucket with the specified name.
     *
     * @param bucketName The name of the bucket to create.
     * @throws IllegalArgumentException if the bucket name is invalid.
     * @throws S3Exception if there's an error creating the bucket.
     */
    void createBucket(String bucketName);

    /**
     * Lists S3 buckets that match the given filter predicate.
     *
     * @param nameFilter A predicate to filter bucket names.
     * @return A list of bucket names that match the filter.
     * @throws S3Exception if there's an error listing the buckets.
     */
    List<String> listBuckets(Predicate<String> nameFilter);

    /**
     * Lists all S3 buckets accessible to the authenticated user.
     *
     * @return A list of all bucket names.
     * @throws S3Exception if there's an error listing the buckets.
     */
    List<String> listBuckets();

    /**
     * Copies settings from a source bucket to a target bucket without copying the contents.
     *
     * @param sourceBucket The name of the source bucket to copy settings from.
     * @param targetBucket The name of the target bucket to apply settings to.
     * @throws IllegalArgumentException if either bucket name is invalid.
     * @throws S3Exception if there's an error accessing or modifying the buckets.
     */
    void copyBucketSettings(String sourceBucket, String targetBucket);
}
