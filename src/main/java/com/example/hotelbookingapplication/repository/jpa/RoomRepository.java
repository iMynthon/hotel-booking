package com.example.hotelbookingapplication.repository.jpa;

import com.example.hotelbookingapplication.model.jpa.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer>, JpaSpecificationExecutor<Room> {

    @EntityGraph(attributePaths = {"hotel"})
    Optional<Room> findByName(String name);

    @EntityGraph(attributePaths = {"hotel"})
    Optional<Room> findByNumber(Integer number);
}
