package org.example.aicao.teaching;

import org.example.aicao.common.page.PageRequest;
import org.example.aicao.common.page.PageResult;
import org.example.aicao.config.TeachingProperties;
import org.example.aicao.llm.ChatCompletionClient;
import org.example.aicao.llm.LlmMessage;
import org.example.aicao.teaching.api.ChatRequest;
import org.example.aicao.teaching.api.ChatResponse;
import org.example.aicao.teaching.api.MessageResponse;
import org.example.aicao.teaching.domain.TeachingMessage;
import org.example.aicao.teaching.domain.TeachingMessageRoles;
import org.example.aicao.teaching.domain.TeachingSession;
import org.example.aicao.teaching.mapper.TeachingMessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TeachingConversationService {

    private final TeachingSessionService teachingSessionService;
    private final TeachingMessageMapper teachingMessageMapper;
    private final ChatCompletionClient chatCompletionClient;
    private final TeachingSystemPromptFactory teachingSystemPromptFactory;
    private final TeachingProperties teachingProperties;

    public TeachingConversationService(
            TeachingSessionService teachingSessionService,
            TeachingMessageMapper teachingMessageMapper,
            ChatCompletionClient chatCompletionClient,
            TeachingSystemPromptFactory teachingSystemPromptFactory,
            TeachingProperties teachingProperties) {
        this.teachingSessionService = teachingSessionService;
        this.teachingMessageMapper = teachingMessageMapper;
        this.chatCompletionClient = chatCompletionClient;
        this.teachingSystemPromptFactory = teachingSystemPromptFactory;
        this.teachingProperties = teachingProperties;
    }

    public PageResult<MessageResponse> listMessages(long sessionId, PageRequest pageRequest) {
        teachingSessionService.requireSession(sessionId);
        long total = teachingMessageMapper.countBySessionId(sessionId);
        List<TeachingMessage> rows = teachingMessageMapper.selectPageBySessionId(
                sessionId,
                pageRequest.offset(),
                pageRequest.size()
        );
        return PageResult.of(rows.stream().map(this::toResponse).toList(), total, pageRequest);
    }

    @Transactional
    public ChatResponse chat(long sessionId, ChatRequest request) {
        TeachingSession session = teachingSessionService.requireSession(sessionId);
        String userText = request.content().trim();

        List<LlmMessage> payload = buildCompletionMessages(session, userText);
        TeachingMessage userRow = persistMessage(sessionId, TeachingMessageRoles.USER, userText);
        String assistantText = chatCompletionClient.complete(payload);
        TeachingMessage assistantRow = persistMessage(sessionId, TeachingMessageRoles.ASSISTANT, assistantText);

        return new ChatResponse(toResponse(userRow), toResponse(assistantRow));
    }

    /**
     * 在持久化本轮用户消息之前读取历史，避免把当前提问重复拼进 messages。
     */
    private List<LlmMessage> buildCompletionMessages(TeachingSession session, String latestUserText) {
        String system = teachingSystemPromptFactory.build(session.getSubjectCode());
        List<LlmMessage> messages = new ArrayList<>();
        messages.add(new LlmMessage(TeachingMessageRoles.SYSTEM, system));

        int historyLimit = teachingProperties.maxContextMessages();
        List<TeachingMessage> latest = teachingMessageMapper.selectLatestBySessionId(session.getId(), historyLimit);
        Collections.reverse(latest);
        for (TeachingMessage row : latest) {
            if (TeachingMessageRoles.USER.equals(row.getRole())) {
                messages.add(new LlmMessage(TeachingMessageRoles.USER, row.getContent()));
            } else if (TeachingMessageRoles.ASSISTANT.equals(row.getRole())) {
                messages.add(new LlmMessage(TeachingMessageRoles.ASSISTANT, row.getContent()));
            }
        }
        messages.add(new LlmMessage(TeachingMessageRoles.USER, latestUserText));
        return messages;
    }

    private TeachingMessage persistMessage(long sessionId, String role, String content) {
        TeachingMessage row = new TeachingMessage();
        row.setSessionId(sessionId);
        row.setRole(role);
        row.setContent(content);
        teachingMessageMapper.insert(row);
        return row;
    }

    private MessageResponse toResponse(TeachingMessage row) {
        return new MessageResponse(row.getId(), row.getRole(), row.getContent(), row.getCreatedAt());
    }
}
