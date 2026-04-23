package com.edumanage.timetableservice.controller;

import com.edumanage.timetableservice.dto.RoomRequest;
import com.edumanage.timetableservice.dto.TimeSlotRequest;
import com.edumanage.timetableservice.dto.TimeSlotResponse;
import com.edumanage.timetableservice.model.Room;
import com.edumanage.timetableservice.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@Tag(name = "Timetable", description = "Schedule and room management")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/slots")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new time slot (with conflict detection)")
    public TimeSlotResponse createSlot(@Valid @RequestBody TimeSlotRequest request) {
        return timetableService.createTimeSlot(request);
    }

    @GetMapping("/slots")
    @Operation(summary = "List all time slots")
    public List<TimeSlotResponse> getAllSlots() {
        return timetableService.getAll();
    }

    @GetMapping("/slots/course/{courseId}")
    @Operation(summary = "Get schedule for a course")
    public List<TimeSlotResponse> getByCourse(@PathVariable UUID courseId) {
        return timetableService.getByCourse(courseId);
    }

    @GetMapping("/slots/teacher/{teacherId}")
    @Operation(summary = "Get schedule for a teacher")
    public List<TimeSlotResponse> getByTeacher(@PathVariable UUID teacherId) {
        return timetableService.getByTeacher(teacherId);
    }

    @DeleteMapping("/slots/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a time slot")
    public void deleteSlot(@PathVariable UUID id) {
        timetableService.deleteTimeSlot(id);
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new room")
    public Room createRoom(@Valid @RequestBody RoomRequest request) {
        return timetableService.createRoom(request);
    }

    @GetMapping("/rooms")
    @Operation(summary = "List all rooms")
    public List<Room> getAllRooms() {
        return timetableService.getAllRooms();
    }
}
