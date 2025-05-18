package com.example.hotelbookingapplication.repository.mongodb;

import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.time.LocalDate;
import java.util.Optional;

public interface BookingRegistrationStatsRepository extends MongoRepository<BookingRegistrationStats,String> {

    Optional<BookingRegistrationStats> findByArrivalDateAndDepartureDate(LocalDate arrivalDate, LocalDate departureDate);
}
