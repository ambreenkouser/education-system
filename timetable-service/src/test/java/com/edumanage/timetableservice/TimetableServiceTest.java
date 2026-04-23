package com.edumanage.timetableservice;

import com.edumanage.timetableservice.dto.TimeSlotRequest;
import com.edumanage.timetableservice.exception.ConflictException;
import com.edumanage.timetableservice.mapper.TimetableMapper;
import com.edumanage.timetableservice.model.DayOfWeek;
import com.edumanage.timetableservice.model.Room;
import com.edumanage.timetableservice.model.TimeSlot;
import com.edumanage.timetableservice.repository.RoomRepository;
import com.edumanage.timetableservice.repository.TimeSlotRepository;
import com.edumanage.timetableservice.service.TimetableService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock TimeSlotRepository timeSlotRepository;
    @Mock RoomRepository roomRepository;
    @Mock TimetableMapper timetableMapper;
    @InjectMocks TimetableService timetableService;

    @Test
    void createSlot_withTeacherConflict_throwsConflictException() {
        UUID teacherId = UUID.randomUUID();
        UUID roomId    = UUID.randomUUID();

        TimeSlotRequest req = new TimeSlotRequest();
        req.setCourseId(UUID.randomUUID());
        req.setTeacherId(teacherId);
        req.setRoomId(roomId);
        req.setDayOfWeek(DayOfWeek.MONDAY);
        req.setStartTime(LocalTime.of(9, 0));
        req.setEndTime(LocalTime.of(10, 0));

        Room room = Room.builder().id(roomId).name("Lab-1").capacity(30).build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(timeSlotRepository.findTeacherConflicts(any(), any(), any(), any()))
                .thenReturn(List.of(TimeSlot.builder().build()));

        assertThatThrownBy(() -> timetableService.createTimeSlot(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Teacher already has a class");
    }

    @Test
    void createSlot_withRoomConflict_throwsConflictException() {
        UUID teacherId = UUID.randomUUID();
        UUID roomId    = UUID.randomUUID();

        TimeSlotRequest req = new TimeSlotRequest();
        req.setCourseId(UUID.randomUUID());
        req.setTeacherId(teacherId);
        req.setRoomId(roomId);
        req.setDayOfWeek(DayOfWeek.TUESDAY);
        req.setStartTime(LocalTime.of(11, 0));
        req.setEndTime(LocalTime.of(12, 0));

        Room room = Room.builder().id(roomId).name("Hall-A").capacity(100).build();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(timeSlotRepository.findTeacherConflicts(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(timeSlotRepository.findRoomConflicts(any(), any(), any(), any()))
                .thenReturn(List.of(TimeSlot.builder().build()));

        assertThatThrownBy(() -> timetableService.createTimeSlot(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already booked");
    }
}
