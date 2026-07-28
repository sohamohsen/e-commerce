package com.task.ecommerce.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsPropertiesConfig {
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String bucketName;
    }
}