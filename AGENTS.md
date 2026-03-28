# Repository Guidelines

Repository: `coachly-feedback-be`

## Project Structure & Module Organization
This repository is a single-module Spring Boot backend for the Coachly feedback domain.

- `src/main/java/it/aredegalli/coachly/feedback/`: application code (`controller`, `controller/request`, `service`, `service/impl`, `repository`, `model`, `dto`, `mapper`, plus `config`, `security`, `exception`, `time`, `util`).
- `src/main/resources/`: runtime configuration (`application.yml`) and Flyway migrations (`db/migration`).
- `src/test/java/it/aredegalli/coachly/feedback/`: unit and integration tests.
- `docs/`: architecture and workflow notes.
- `docker-compose.yml`: local dependencies for development.

Keep production/test package structures aligned.
If you add new packages, keep them under `it.aredegalli.coachly.feedback`.

## Build, Test, and Development Commands
Use Maven Wrapper from repo root:

- `.\\mvnw.cmd spring-boot:run`: run locally (Windows).
- `.\\mvnw.cmd test`: run tests.
- `.\\mvnw.cmd clean verify`: full build checks.
- `.\\mvnw.cmd clean package`: create JAR in `target/`.

## Coding Style & Naming Conventions
- Java 21, Spring Boot conventions, layered architecture aligned with other Coachly BE services.
- Class names `PascalCase`, methods/fields `camelCase`, constants and enum values `UPPER_SNAKE_CASE`.
- Keep business logic in services, controllers thin.
- Services must expose an interface in `service` and implementation in `service/impl` with `*ServiceImpl` naming.
- Prefer records for DTOs and explicit mappers.
- Preserve existing formatting style in touched files.

## Testing Guidelines
- Framework: JUnit 5 (`spring-boot-starter-test`).
- Integration tests: Testcontainers PostgreSQL.
- Naming: `*Test` / `*Tests`.
- Add/update tests for every behavior change.

## Commit & Pull Request Guidelines
- Use Conventional Commits (e.g. `feat(featurerequest): add vote use case and counters`).
- Keep commits scoped and atomic.
- PR must include: summary, rationale, test evidence, and schema/config impact.

## Agent Workflow (Mandatory)
- Always use SerenaMCP as primary workflow:
  - activate project `coachly-feedback-be` and verify onboarding state at session start;
  - use Serena tools for discovery/navigation before edits.
- Always read relevant files under `docs/` before implementation.
- Always update `docs/` when architecture, contracts, workflows, or integration assumptions change.
- Always create a commit at the end of each requested implementation step unless explicitly told not to.
- Keep working tree clean after each commit.
