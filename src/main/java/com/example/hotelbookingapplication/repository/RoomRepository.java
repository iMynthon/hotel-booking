package com.example.hotelbookingapplication.repository;

import com.example.hotelbookingapplication.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {

    Optional<Room> findByName(String name);
}
