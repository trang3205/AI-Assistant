package com.example.aiassistant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
// Cấu hình WebClient dùng kết nối HTTP bất đồng bộ tới LM Studio
public class WebClientConfig {

    @Value("${lmstudio.base-url}")
    private String baseUrl;

    // Khởi tạo WebClient với base URL và dung lượng bộ nhớ đệm mở rộng
    @Bean
    public WebClient lmStudioWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }
}