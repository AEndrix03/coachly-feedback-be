package it.aredegalli.coachly.feedback.security;

import it.aredegalli.coachly.feedback.config.AppProperties;
import it.aredegalli.coachly.feedback.exception.BadRequestException;
import it.aredegalli.coachly.feedback.model.AppRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;
import java.util.UUID;

@Component
public class RequestUserContextResolver {

    private final AppProperties appProperties;

    public RequestUserContextResolver(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public RequestUserContext getRequiredContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BadRequestException("REQUEST_CONTEXT_MISSING", "No active request context");
        }
        return resolve(attributes.getRequest());
    }

    public RequestUserContext resolve(HttpServletRequest request) {
        String userIdHeader = request.getHeader(appProperties.security().userId());
        String userRoleHeader = request.getHeader(appProperties.security().userRole());
        if (userIdHeader == null) {
            throw new BadRequestException("TRUSTED_HEADERS_MISSING", "Required trusted headers are missing");
        }
        UUID userId = UUID.fromString(userIdHeader);
        AppRole role = resolveRole(userRoleHeader);
        return new RequestUserContext(userId, role, request.getHeader("X-User-Name"));
    }

    private AppRole resolveRole(String userRoleHeader) {
        if (userRoleHeader == null || userRoleHeader.isBlank()) {
            return AppRole.USER;
        }

        String normalized = userRoleHeader
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');

        String[] roles = normalized.split(",");
        for (String role : roles) {
            String trimmed = role.trim();
            if (trimmed.equals("ADMIN")) {
                return AppRole.ADMIN;
            }
        }
        for (String role : roles) {
            String trimmed = role.trim();
            if (trimmed.equals("MODERATOR")) {
                return AppRole.MODERATOR;
            }
        }

        return AppRole.USER;
    }
}


