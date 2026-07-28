package com.task.ecommerce.service;

import com.task.ecommerce.config.properties.AwsPropertiesConfig;
import com.task.ecommerce.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    private final S3Client s3Client;
    private final AwsPropertiesConfig awsProperties;

    public String uploadProductImage(MultipartFile file) {
        validate(file);

        String key = "products/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        String bucket = getBucketName();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image.");
        }

        return buildPublicUrl(key);
    }

    public void deleteProductImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String key = extractKeyFromUrl(imageUrl);
        if (key == null) return;

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(getBucketName())
                    .key(key)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete old S3 image: {}", imageUrl, e);
        }
    }

    private String getBucketName() {
        if (awsProperties.getS3() != null && awsProperties.getS3().getBucketName() != null && !awsProperties.getS3().getBucketName().isBlank()) {
            return awsProperties.getS3().getBucketName();
        }
        return "ecommerce-bucket";
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, or WEBP images are allowed.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("Image must be smaller than 5MB.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }

    private String buildPublicUrl(String key) {
        String bucket = getBucketName();
        String region = (awsProperties.getRegion() != null && !awsProperties.getRegion().isBlank())
                ? awsProperties.getRegion() : "us-east-1";
        return "https://%s.s3.%s.amazonaws.com/%s"
                .formatted(bucket, region, key);
    }

    private String extractKeyFromUrl(String imageUrl) {
        int idx = imageUrl.indexOf(".amazonaws.com/");
        if (idx == -1) return null;
        return imageUrl.substring(idx + ".amazonaws.com/".length());
    }
}