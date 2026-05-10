package org.example.aicao.llm;

import org.example.aicao.config.LlmProperties;
import org.example.aicao.llm.dto.ChatCompletionRequest;
import org.example.aicao.llm.dto.ChatCompletionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class RestChatCompletionClient implements ChatCompletionClient {

    private final RestClient restClient;
    private final LlmProperties llmProperties;

    public RestChatCompletionClient(RestClient llmRestClient, LlmProperties llmProperties) {
        this.restClient = llmRestClient;
        this.llmProperties = llmProperties;
    }

    @Override
    public String complete(List<LlmMessage> messages) {
        if (llmProperties.apiKey() == null || llmProperties.apiKey().isBlank()) {
            throw new IllegalStateException("未配置大模型 API 密钥：请设置环境变量 DEEPSEEK_API_KEY（见 application.yml）");
        }

        ChatCompletionRequest request = new ChatCompletionRequest(
                llmProperties.model(),
                messages,
                llmProperties.temperature()
        );

        try {
            ChatCompletionResponse response = restClient.post()
                    .uri(llmProperties.chatCompletionsPath())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + llmProperties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new IllegalStateException("大模型返回空结果");
            }
            ChatCompletionResponse.Message message = response.choices().get(0).message();
            if (message == null || message.content() == null) {
                throw new IllegalStateException("大模型返回消息体为空");
            }
            return message.content();
        } catch (RestClientException ex) {
            throw new IllegalStateException("调用大模型接口失败：" + ex.getMessage(), ex);
        }
    }
}
