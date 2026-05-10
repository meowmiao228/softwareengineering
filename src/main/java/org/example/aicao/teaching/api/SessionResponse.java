package org.example.aicao.teaching.api;

import java.time.Instant;

public record SessionResponse(long id, String title, String subjectCode, Instant createdAt) {
}
