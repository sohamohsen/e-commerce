package com.task.ecommerce.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AiTestController {

    private final AiClient aiClient;

    @GetMapping("/ai-test")
    public String test(@RequestParam(defaultValue = "what is spring boot one sentence") String prompt) {
        log.info(prompt);
        return aiClient.generateText(prompt);
    }
}