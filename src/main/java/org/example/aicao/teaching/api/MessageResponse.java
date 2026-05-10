package org.example.aicao.teaching.api;

import java.time.Instant;

public record MessageResponse(long id, String role, String content, Instant createdAt) {
}
