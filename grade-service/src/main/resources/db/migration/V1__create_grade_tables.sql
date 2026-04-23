CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE exams (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    course_id   UUID        NOT NULL,
    title       VARCHAR(255) NOT NULL,
    exam_date   DATE         NOT NULL,
    total_marks NUMERIC(6,2) NOT NULL CHECK (total_marks > 0),
    type        VARCHAR(20)  NOT NULL
                             CHECK (type IN ('QUIZ','MIDTERM','FINAL','ASSIGNMENT','PROJECT')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE grade_records (
    id             UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id     UUID         NOT NULL,
    exam_id        UUID         NOT NULL REFERENCES exams(id),
    marks_obtained NUMERIC(6,2) NOT NULL CHECK (marks_obtained >= 0),
    grade_letter   VARCHAR(2)   NOT NULL,
    grade_points   NUMERIC(3,1) NOT NULL,
    graded_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, exam_id)
);

CREATE TABLE outbox_events (
    id           UUID      NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    topic        VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    payload      TEXT         NOT NULL,
    published    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP
);

CREATE INDEX idx_exams_course          ON exams(course_id);
CREATE INDEX idx_grades_student        ON grade_records(student_id);
CREATE INDEX idx_grades_exam           ON grade_records(exam_id);
CREATE INDEX idx_outbox_unpublished    ON outbox_events(published) WHERE published = FALSE;
