-- Enforce fixed grade level values to prevent inconsistent report filtering
-- e.g. prevent mixing "Grade 10", "10", "X", "GRADE_10"
ALTER TABLE students
    ADD CONSTRAINT chk_grade_level
    CHECK (grade_level IN (
        'GRADE_1','GRADE_2','GRADE_3','GRADE_4','GRADE_5','GRADE_6',
        'GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12'
    ));

-- Update any existing non-conforming rows before the constraint lands
-- (run manually in production before applying migration if existing data needs cleansing)
