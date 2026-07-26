package com.task.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient paymobWebClient() {
        return WebClient.builder()
                .baseUrl("https://accept.paymob.com")
                .build();
    }
}