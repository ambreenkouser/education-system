CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE attendance_records (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id      UUID        NOT NULL,
    course_id       UUID        NOT NULL,
    attendance_date DATE        NOT NULL,
    status          VARCHAR(10) NOT NULL
                                CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED')),
    marked_by       UUID        NOT NULL,
    remarks         TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, course_id, attendance_date)
);

CREATE INDEX idx_attendance_student      ON attendance_records(student_id);
CREATE INDEX idx_attendance_course_date  ON attendance_records(course_id, attendance_date);
CREATE INDEX idx_attendance_date         ON attendance_records(attendance_date);
