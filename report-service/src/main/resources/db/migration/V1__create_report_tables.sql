CREATE TABLE report_student_summary (
    student_id        UUID PRIMARY KEY,
    student_code      VARCHAR(50)  NOT NULL,
    total_enrollments INT          NOT NULL DEFAULT 0,
    last_updated      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE report_attendance_summary (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id    UUID    NOT NULL,
    course_id     UUID    NOT NULL,
    total_classes INT     NOT NULL DEFAULT 0,
    present_count INT     NOT NULL DEFAULT 0,
    absent_count  INT     NOT NULL DEFAULT 0,
    late_count    INT     NOT NULL DEFAULT 0,
    last_updated  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, course_id)
);

CREATE INDEX idx_att_summary_course ON report_attendance_summary(course_id);

CREATE TABLE report_grade_summary (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id     UUID           NOT NULL,
    exam_id        UUID           NOT NULL,
    exam_title     VARCHAR(200)   NOT NULL,
    marks_obtained DOUBLE PRECISION,
    grade_letter   VARCHAR(3),
    grade_points   DOUBLE PRECISION,
    graded_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_grade_summary_student ON report_grade_summary(student_id);

CREATE TABLE report_fee_summary (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id      UUID           NOT NULL,
    invoice_id      UUID           NOT NULL,
    paid_amount     NUMERIC(10,2)  NOT NULL,
    payment_method  VARCHAR(50)    NOT NULL,
    transaction_id  VARCHAR(100)   NOT NULL UNIQUE,
    paid_at         TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fee_summary_student ON report_fee_summary(student_id);
