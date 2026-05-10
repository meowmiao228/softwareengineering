package org.example.aicao.teaching.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSessionRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 64) String subjectCode
) {
}
