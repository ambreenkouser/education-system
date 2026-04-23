package com.edumanage.timetableservice.repository;

import com.edumanage.timetableservice.model.DayOfWeek;
import com.edumanage.timetableservice.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    List<TimeSlot> findByCourseId(UUID courseId);
    List<TimeSlot> findByTeacherId(UUID teacherId);

    // Detect teacher conflict: same teacher, same day, overlapping time
    @Query("""
        SELECT ts FROM TimeSlot ts
        WHERE ts.teacherId = :teacherId
          AND ts.dayOfWeek = :day
          AND ts.startTime < :endTime
          AND ts.endTime   > :startTime
    """)
    List<TimeSlot> findTeacherConflicts(UUID teacherId, DayOfWeek day,
                                        LocalTime startTime, LocalTime endTime);

    // Detect room conflict: same room, same day, overlapping time
    @Query("""
        SELECT ts FROM TimeSlot ts
        WHERE ts.room.id = :roomId
          AND ts.dayOfWeek = :day
          AND ts.startTime < :endTime
          AND ts.endTime   > :startTime
    """)
    List<TimeSlot> findRoomConflicts(UUID roomId, DayOfWeek day,
                                     LocalTime startTime, LocalTime endTime);
}
