package com.task.ecommerce;

import com.task.ecommerce.config.properties.AwsPropertiesConfig;
import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({PaymobPropertiesConfig.class, AwsPropertiesConfig.class})
@EnableScheduling
@EnableAsync
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
