package com.example.aiassistant.service;

import com.example.aiassistant.dto.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LmStudioService {

    private final WebClient lmStudioWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${lmstudio.model}")
    private String model;

    @Value("${lmstudio.temperature}")
    private double temperature;

    @Value("${lmstudio.max-tokens}")
    private int maxTokens;

    /**
     * Trả về Mono<String> — reactive thuần, không block
     */
    public Mono<String> chat(List<Message> messages) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages,
                "temperature", temperature,
                "max_tokens", maxTokens);

        return lmStudioWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(raw -> {
                    try {
                        log.debug("LM Studio raw response: {}", raw);
                        JsonNode root = objectMapper.readTree(raw);
                        return root.path("choices").get(0)
                                .path("message").path("content").asText();
                    } catch (Exception e) {
                        log.error("Lỗi parse JSON", e);
                        throw new RuntimeException("Parse JSON thất bại: " + e.getMessage());
                    }
                })
                .doOnError(e -> log.error("Lỗi gọi LM Studio", e))
                .onErrorMap(e -> new RuntimeException("Không gọi được LM Studio: " + e.getMessage()));
    }
}