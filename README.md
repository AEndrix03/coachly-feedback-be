# coachly-feedback-be

Standalone Spring Boot microservice for Coachly user feedback lifecycle: ratings/reviews, feature requests, comments, votes, polls, moderation, roadmap history, and analytics.

## Overview

This service is designed to run behind Coachly API Gateway.
Gateway authentication is trusted, and user context is resolved from headers:
- `X-User-Id`
- `X-User-Role`
- `X-User-Name` (optional)

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- springdoc OpenAPI
- JUnit 5
- Testcontainers
- Maven

## Architecture

Package root: `com.coachly.feedback`

Vertical modules:
- `common`
- `feedback`
- `featurerequest`
- `comment`
- `poll`
- `moderation`
- `analytics`
- `roadmap`

Cross-cutting:
- `RequestUserContextResolver` for trusted headers
- `GlobalExceptionHandler` for uniform errors
- `ApiResponse<T>` and `PagedResponse<T>` wrappers
- `AuditableEntity` with `createdAt`, `updatedAt`, `deletedAt`

## API

Base path: `/api/v1`

Main endpoint groups:
- Feedback: `/feedback`, `/feedback/summary`
- Feature requests: `/feature-requests`, `/feature-requests/{id}/vote`
- Admin feature roadmap: `/admin/feature-requests/{id}/status`, `/duplicate-of`
- Comments: `/comments`, `/comments/{id}/vote`
- Polls: `/polls`, `/polls/{id}/responses`, admin publish/close
- Moderation: `/reports`, `/admin/reports`, `/admin/content/{targetType}/{targetId}`
- Analytics: `/admin/analytics/*`

OpenAPI UI:
- `http://localhost:8085/swagger-ui.html`

## Local Run

1. Start PostgreSQL:
```bash
docker compose up -d postgres
```

2. Run service:
```bash
./mvnw spring-boot:run
```
Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

3. Health:
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

## Environment

Use `.env.example` as reference.

Main vars:
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `PORT`

## Database

Flyway migration:
- `src/main/resources/db/migration/V1__init_schema.sql`

Includes:
- all core tables
- unique constraints for votes/responses
- indexes for filtering and list performance

## Tests

Run:
```bash
./mvnw test
```

Coverage includes:
- policy unit tests
- integration tests with PostgreSQL Testcontainers

## Future Extensions (already prepared)

- `FeatureRankingStrategy` abstraction
- domain event publisher (`DomainEventPublisher`)
- notification extension point (`NotificationHook`, currently no-op)
- modular boundaries ready for Redis/Kafka adapters