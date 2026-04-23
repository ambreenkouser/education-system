CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE courses (
    id              UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    credits         INT          NOT NULL CHECK (credits BETWEEN 1 AND 10),
    teacher_id      UUID,
    max_students    INT          NOT NULL CHECK (max_students > 0),
    enrolled_count  INT          NOT NULL DEFAULT 0 CHECK (enrolled_count >= 0),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                                 CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED')),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE subjects (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    course_id   UUID         NOT NULL REFERENCES courses(id) ON DELETE CASCADE
);

CREATE INDEX idx_courses_code      ON courses(code);
CREATE INDEX idx_courses_status    ON courses(status);
CREATE INDEX idx_courses_teacher   ON courses(teacher_id);
CREATE INDEX idx_subjects_course   ON subjects(course_id);
