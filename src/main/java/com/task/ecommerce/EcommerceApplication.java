package com.task.ecommerce;

import com.task.ecommerce.config.properties.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({PaymobPropertiesConfig.class, AwsPropertiesConfig.class, HuggingFacePropertiesConfig.class, KeycloakAdminProperties.class,  KeycloakLoginProperties.class})
@EnableScheduling
@EnableAsync
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
