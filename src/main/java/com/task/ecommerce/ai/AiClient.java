package com.task.ecommerce.ai;

import com.task.ecommerce.config.properties.HuggingFacePropertiesConfig;
import com.task.ecommerce.ai.dto.ChatCompletionRequest;
import com.task.ecommerce.ai.dto.ChatCompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final HuggingFacePropertiesConfig huggingFaceProperties;

    public String generateText(String prompt) {

        RestClient restClient = RestClient.create(huggingFaceProperties.getBaseUrl());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(huggingFaceProperties.getModel())
                .messages(List.of(
                        ChatCompletionRequest.Message.builder().role("user").content(prompt).build()
                ))
                .build();

        ChatCompletionResponse response = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + huggingFaceProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("Empty response from Hugging Face");
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}