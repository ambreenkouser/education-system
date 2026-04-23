CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE user_profiles (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id     UUID         NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    phone       VARCHAR(30),
    address     TEXT,
    user_type   VARCHAR(20)  NOT NULL
                             CHECK (user_type IN ('ADMIN','TEACHER','STUDENT','PARENT')),
    avatar_url  VARCHAR(500),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_user_profiles_email   ON user_profiles(email);
CREATE INDEX idx_user_profiles_type    ON user_profiles(user_type);
