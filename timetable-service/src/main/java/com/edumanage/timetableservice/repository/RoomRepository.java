package com.edumanage.timetableservice.repository;

import com.edumanage.timetableservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    Optional<Room> findByName(String name);
    boolean existsByName(String name);
}
