package com.coachly.feedback.common.security;

import com.coachly.feedback.common.model.AppRole;

import java.util.UUID;

public record RequestUserContext(UUID userId, AppRole role, String userName) {
}