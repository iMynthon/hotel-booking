package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.service.mongodb.BookingRegistrationStatService;
import com.example.hotelbookingapplication.service.mongodb.RegistrationUserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsDataController {

    private final BookingRegistrationStatService bookingRegistrationStatService;

    private final RegistrationUserStatsService registrationUserStatsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users")
    public void statisticsUserFileRecorded() {
        registrationUserStatsService.dataRecordToFile();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/bookings")
    public void statisticsBookingFileRecorded() {
        bookingRegistrationStatService.dataRecordToFile();
    }
}
