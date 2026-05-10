package org.example.aicao.llm;

import java.util.List;

/**
 * 依赖倒置：教学领域只依赖该接口，便于切换 DeepSeek / Kimi / 其他兼容实现。
 */
public interface ChatCompletionClient {

    String complete(List<LlmMessage> messages);
}
