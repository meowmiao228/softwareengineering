package org.example.aicao.llm;

/**
 * OpenAI 兼容 Chat Completions 单条消息。
 */
public record LlmMessage(String role, String content) {
}
