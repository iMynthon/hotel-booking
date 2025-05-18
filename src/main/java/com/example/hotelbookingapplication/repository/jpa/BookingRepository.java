package com.example.hotelbookingapplication.repository.jpa;

import com.example.hotelbookingapplication.model.jpa.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> , JpaSpecificationExecutor<Booking> {

    @EntityGraph(attributePaths = {"user","room"})
    Optional<List<Booking>> findByUserId(Integer id);

    Optional<Booking> findByArrivalDateAndDepartureDate(LocalDate arrivalDate,LocalDate departureDate);
}
