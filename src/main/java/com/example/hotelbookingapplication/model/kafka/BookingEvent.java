package com.example.hotelbookingapplication.model.kafka;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record BookingEvent(
        Integer userId,

        LocalDate arrivalDate,

        LocalDate departureDate,

        LocalDateTime registrationBooking
) {
}
