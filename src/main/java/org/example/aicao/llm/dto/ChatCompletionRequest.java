package org.example.aicao.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.aicao.llm.LlmMessage;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionRequest(
        String model,
        List<LlmMessage> messages,
        Double temperature
) {
}
