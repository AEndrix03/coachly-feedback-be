CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE feedback_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    visibility VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body VARCHAR(4000) NOT NULL,
    rating_value INTEGER,
    category VARCHAR(100),
    target_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL,
    feature_key VARCHAR(100),
    screen_key VARCHAR(100),
    flow_key VARCHAR(100),
    platform VARCHAR(30),
    app_version VARCHAR(30),
    severity VARCHAR(20),
    reproducible BOOLEAN,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE feature_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    category VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    duplicate_of_id UUID,
    platform_target VARCHAR(30),
    module_key VARCHAR(100),
    upvotes_count INTEGER NOT NULL DEFAULT 0,
    comments_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE feature_votes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_request_id UUID NOT NULL REFERENCES feature_requests(id),
    user_id UUID NOT NULL,
    vote_type VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_feature_vote_feature_user UNIQUE (feature_request_id, user_id)
);

CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL,
    author_user_id UUID NOT NULL,
    parent_comment_id UUID,
    root_comment_id UUID,
    depth INTEGER NOT NULL,
    body VARCHAR(3000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    replies_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE comment_votes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id UUID NOT NULL REFERENCES comments(id),
    user_id UUID NOT NULL,
    vote_type VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_comment_vote_comment_user UNIQUE (comment_id, user_id)
);

CREATE TABLE polls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    multiple_choice BOOLEAN NOT NULL,
    anonymous_results BOOLEAN NOT NULL,
    opens_at TIMESTAMPTZ NOT NULL,
    closes_at TIMESTAMPTZ NOT NULL,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE poll_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id UUID NOT NULL REFERENCES polls(id),
    label VARCHAR(500) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE poll_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id UUID NOT NULL REFERENCES polls(id),
    user_id UUID NOT NULL,
    submitted_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_poll_response_poll_user UNIQUE (poll_id, user_id)
);

CREATE TABLE poll_response_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_response_id UUID NOT NULL REFERENCES poll_responses(id),
    poll_option_id UUID NOT NULL REFERENCES poll_options(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL,
    old_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by_user_id UUID NOT NULL,
    public_note VARCHAR(1000),
    internal_note VARCHAR(1000),
    changed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE moderation_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_user_id UUID NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL,
    reason VARCHAR(100) NOT NULL,
    details VARCHAR(2000),
    status VARCHAR(20) NOT NULL,
    resolved_at TIMESTAMPTZ,
    resolved_by_user_id UUID,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_feedback_target ON feedback_entries (target_type, target_id);
CREATE INDEX idx_feature_request_status_created ON feature_requests (status, created_at DESC);
CREATE INDEX idx_comments_target_parent ON comments (target_type, target_id, parent_comment_id);
CREATE INDEX idx_polls_status_window ON polls (status, opens_at, closes_at);
CREATE INDEX idx_status_history_target ON status_history (target_type, target_id, changed_at DESC);