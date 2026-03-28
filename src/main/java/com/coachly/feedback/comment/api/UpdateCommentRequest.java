package com.coachly.feedback.comment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(@NotBlank @Size(max = 3000) String body) {
}