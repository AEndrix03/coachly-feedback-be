package it.aredegalli.coachly.feedback.security;

import it.aredegalli.coachly.feedback.exception.ForbiddenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorizationService {

    public void requireAdmin(RequestUserContext context) {
        if (!context.role().isAdmin()) {
            throw new ForbiddenException("ADMIN_REQUIRED", "Admin role is required");
        }
    }

    public void requireModeratorOrAdmin(RequestUserContext context) {
        if (!context.role().canModerate()) {
            throw new ForbiddenException("MODERATOR_REQUIRED", "Moderator or Admin role is required");
        }
    }

    public void requireOwnerOrModerator(RequestUserContext context, UUID ownerUserId) {
        if (!ownerUserId.equals(context.userId()) && !context.role().canModerate()) {
            throw new ForbiddenException("OWNER_OR_MODERATOR_REQUIRED", "Operation not allowed for current user");
        }
    }
}


