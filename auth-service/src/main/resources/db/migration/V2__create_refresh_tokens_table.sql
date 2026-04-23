CREATE TABLE refresh_tokens (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    token       VARCHAR(512) NOT NULL UNIQUE,
    user_id     UUID         NOT NULL,
    expiry_date TIMESTAMP    NOT NULL
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);
