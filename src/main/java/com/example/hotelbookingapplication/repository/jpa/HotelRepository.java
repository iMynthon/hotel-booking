package com.example.hotelbookingapplication.repository.jpa;

import com.example.hotelbookingapplication.model.jpa.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Integer>, JpaSpecificationExecutor<Hotel> {

    Optional<Hotel> findByNameIgnoreCase(String name);
}
