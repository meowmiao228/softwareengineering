package org.example.aicao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 任意 OpenAI 兼容 Chat Completions 服务（默认 DeepSeek）的接入参数。
 */
@Validated
@ConfigurationProperties(prefix = "llm")
public record LlmProperties(
        String apiBaseUrl,
        String apiKey,
        String model,
        String chatCompletionsPath,
        Double temperature
) {
    public LlmProperties {
        if (temperature == null) {
            temperature = 0.3;
        }
    }
}
