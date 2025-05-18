package com.example.hotelbookingapplication.model.mongodb;

import com.example.hotelbookingapplication.model.jpa.Booking;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Document(collection = "booking_data")
@Builder
@ToString
public class BookingRegistrationStats {

    private String id;

    private Integer userId;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private LocalDateTime registeredTime;

    @Transient
    private Booking booking;
}
