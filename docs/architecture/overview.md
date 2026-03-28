# Architecture Notes

## Current Scope

Implemented bounded contexts:
- Feedback entries (review/general/bug/contextual)
- Feature requests and votes
- Threaded comments and votes
- Polls and responses
- Moderation reports and content hide/restore
- Roadmap status history
- Admin analytics views

## Security Model

The service trusts gateway headers and does not manage JWT tokens internally.
Authorization checks are enforced in application services.

## Persistence Principles

- UUID primary keys
- soft-delete support via `deleted_at`
- denormalized counters for feature votes/comments and comment score
- SQL-based analytics and ranking

## Evolution Path

- Introduce Redis-backed ranking with alternate `FeatureRankingStrategy`
- Add Kafka/outbox through `DomainEventPublisher` adapter
- Add moderation AI policy implementation behind dedicated interfaces