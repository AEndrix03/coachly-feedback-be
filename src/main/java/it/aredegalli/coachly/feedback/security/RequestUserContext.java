package it.aredegalli.coachly.feedback.security;

import it.aredegalli.coachly.feedback.model.AppRole;

import java.util.UUID;

public record RequestUserContext(UUID userId, AppRole role, String userName) {
}


