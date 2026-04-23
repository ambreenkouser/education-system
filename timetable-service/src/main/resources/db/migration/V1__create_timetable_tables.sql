CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE rooms (
    id       UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE,
    capacity INT          NOT NULL CHECK (capacity > 0),
    building VARCHAR(100),
    floor    VARCHAR(20)
);

CREATE TABLE time_slots (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    course_id   UUID        NOT NULL,
    teacher_id  UUID        NOT NULL,
    room_id     UUID        NOT NULL REFERENCES rooms(id),
    day_of_week VARCHAR(10) NOT NULL
                            CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY',
                                                   'THURSDAY','FRIDAY','SATURDAY')),
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_time_order CHECK (end_time > start_time)
);

CREATE INDEX idx_slots_course   ON time_slots(course_id);
CREATE INDEX idx_slots_teacher  ON time_slots(teacher_id);
CREATE INDEX idx_slots_room_day ON time_slots(room_id, day_of_week);
CREATE INDEX idx_slots_teacher_day ON time_slots(teacher_id, day_of_week);
