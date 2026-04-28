-- Audit trail: who last updated each user account and when it was disabled
ALTER TABLE users
    ADD COLUMN updated_at   TIMESTAMP,
    ADD COLUMN deleted_at   TIMESTAMP;  -- soft delete: non-null = disabled

-- Revoked tokens audit log (primary enforcement is Redis; this provides 90-day audit trail)
CREATE TABLE revoked_tokens (
    jti          VARCHAR(36)  PRIMARY KEY,
    user_id      UUID         NOT NULL,
    revoked_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at   TIMESTAMP    NOT NULL
);

CREATE INDEX idx_revoked_tokens_user_id ON revoked_tokens(user_id);

-- Auto-purge expired rows older than 90 days (run via pg_cron or nightly job)
-- DELETE FROM revoked_tokens WHERE expires_at < NOW() - INTERVAL '90 days';
