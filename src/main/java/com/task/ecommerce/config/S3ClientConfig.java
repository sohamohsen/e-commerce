package com.task.ecommerce.config;

import com.task.ecommerce.config.properties.AwsPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final AwsPropertiesConfig awsProperties;

    @Bean
    public S3Client s3Client() {
        String regionStr = (awsProperties.getRegion() != null && !awsProperties.getRegion().isBlank())
                ? awsProperties.getRegion() : "us-east-1";
        String accessKey = (awsProperties.getAccessKeyId() != null && !awsProperties.getAccessKeyId().isBlank())
                ? awsProperties.getAccessKeyId() : "dummy-access-key";
        String secretKey = (awsProperties.getSecretAccessKey() != null && !awsProperties.getSecretAccessKey().isBlank())
                ? awsProperties.getSecretAccessKey() : "dummy-secret-key";

        return S3Client.builder()
                .region(Region.of(regionStr))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}