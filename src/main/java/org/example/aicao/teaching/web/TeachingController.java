package org.example.aicao.teaching.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.aicao.common.page.PageRequest;
import org.example.aicao.common.page.PageResult;
import org.example.aicao.teaching.api.ChatRequest;
import org.example.aicao.teaching.api.ChatResponse;
import org.example.aicao.teaching.api.CreateSessionRequest;
import org.example.aicao.teaching.api.MessageResponse;
import org.example.aicao.teaching.api.SessionResponse;
import org.example.aicao.teaching.TeachingConversationService;
import org.example.aicao.teaching.TeachingSessionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teaching")
@Validated
public class TeachingController {

    private final TeachingSessionService teachingSessionService;
    private final TeachingConversationService teachingConversationService;

    public TeachingController(
            TeachingSessionService teachingSessionService,
            TeachingConversationService teachingConversationService) {
        this.teachingSessionService = teachingSessionService;
        this.teachingConversationService = teachingConversationService;
    }

    @PostMapping("/sessions")
    public SessionResponse createSession(@RequestBody @Valid CreateSessionRequest request) {
        return teachingSessionService.create(request);
    }

    @GetMapping("/sessions")
    public PageResult<SessionResponse> listSessions(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return teachingSessionService.list(PageRequest.of(page, size));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public PageResult<MessageResponse> listMessages(
            @PathVariable long sessionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        return teachingConversationService.listMessages(sessionId, PageRequest.of(page, size));
    }

    @PostMapping("/sessions/{sessionId}/chat")
    public ChatResponse chat(
            @PathVariable long sessionId,
            @RequestBody @Valid ChatRequest request) {
        return teachingConversationService.chat(sessionId, request);
    }
}
