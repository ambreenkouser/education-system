package com.edumanage.timetableservice.service;

import com.edumanage.timetableservice.dto.RoomRequest;
import com.edumanage.timetableservice.dto.TimeSlotRequest;
import com.edumanage.timetableservice.dto.TimeSlotResponse;
import com.edumanage.timetableservice.exception.ConflictException;
import com.edumanage.timetableservice.exception.ResourceNotFoundException;
import com.edumanage.timetableservice.mapper.TimetableMapper;
import com.edumanage.timetableservice.model.Room;
import com.edumanage.timetableservice.model.TimeSlot;
import com.edumanage.timetableservice.repository.RoomRepository;
import com.edumanage.timetableservice.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimeSlotRepository timeSlotRepository;
    private final RoomRepository roomRepository;
    private final TimetableMapper timetableMapper;

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + request.getRoomId()));

        // Teacher conflict check
        List<TimeSlot> teacherConflicts = timeSlotRepository.findTeacherConflicts(
                request.getTeacherId(), request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime());
        if (!teacherConflicts.isEmpty()) {
            throw new ConflictException(
                    "Teacher already has a class on " + request.getDayOfWeek()
                    + " between " + request.getStartTime() + " and " + request.getEndTime());
        }

        // Room conflict check
        List<TimeSlot> roomConflicts = timeSlotRepository.findRoomConflicts(
                request.getRoomId(), request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime());
        if (!roomConflicts.isEmpty()) {
            throw new ConflictException(
                    "Room " + room.getName() + " is already booked on " + request.getDayOfWeek()
                    + " between " + request.getStartTime() + " and " + request.getEndTime());
        }

        TimeSlot slot = TimeSlot.builder()
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .room(room)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return timetableMapper.toResponse(timeSlotRepository.save(slot));
    }

    public List<TimeSlotResponse> getByCourse(UUID courseId) {
        return timeSlotRepository.findByCourseId(courseId).stream()
                .map(timetableMapper::toResponse).toList();
    }

    public List<TimeSlotResponse> getByTeacher(UUID teacherId) {
        return timeSlotRepository.findByTeacherId(teacherId).stream()
                .map(timetableMapper::toResponse).toList();
    }

    public List<TimeSlotResponse> getAll() {
        return timeSlotRepository.findAll().stream()
                .map(timetableMapper::toResponse).toList();
    }

    @Transactional
    public void deleteTimeSlot(UUID id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new ResourceNotFoundException("TimeSlot not found: " + id);
        }
        timeSlotRepository.deleteById(id);
    }

    @Transactional
    public Room createRoom(RoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new ConflictException("Room already exists: " + request.getName());
        }
        return roomRepository.save(Room.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .build());
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public void handleStudentEnrolled(UUID courseId, UUID studentId) {
        List<TimeSlot> slots = timeSlotRepository.findByCourseId(courseId);
        log.info("Student {} enrolled in course {} — {} timetable slots available",
                studentId, courseId, slots.size());
    }
}
