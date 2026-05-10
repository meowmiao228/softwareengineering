package org.example.aicao.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "teaching")
public record TeachingProperties(
        @Min(2) @Max(64) int maxContextMessages,
        String systemPromptTemplate
) {
}
