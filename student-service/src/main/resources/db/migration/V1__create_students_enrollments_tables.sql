CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE students (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID        NOT NULL UNIQUE,
    student_code    VARCHAR(20) NOT NULL UNIQUE,
    date_of_birth   DATE,
    parent_id       UUID,
    grade_level     VARCHAR(50) NOT NULL,
    enrollment_date TIMESTAMP   NOT NULL DEFAULT NOW(),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','GRADUATED','SUSPENDED'))
);

CREATE TABLE enrollments (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id  UUID        NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id   UUID        NOT NULL,
    enrolled_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    status      VARCHAR(20) NOT NULL DEFAULT 'ENROLLED'
                            CHECK (status IN ('ENROLLED','DROPPED','COMPLETED')),
    UNIQUE (student_id, course_id)
);

CREATE INDEX idx_students_user_id       ON students(user_id);
CREATE INDEX idx_students_student_code  ON students(student_code);
CREATE INDEX idx_students_grade         ON students(grade_level);
CREATE INDEX idx_enrollments_student    ON enrollments(student_id);
CREATE INDEX idx_enrollments_course     ON enrollments(course_id);
