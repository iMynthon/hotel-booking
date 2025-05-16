package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.dto.request.UpsertBookingRequest;
import com.example.hotelbookingapplication.dto.response.AllBookingResponse;
import com.example.hotelbookingapplication.dto.response.BookingResponse;
import com.example.hotelbookingapplication.mapper.BookingMapper;
import com.example.hotelbookingapplication.service.impl.BookingService;
import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
@Validated
public class BookingController {

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public AllBookingResponse findAll(HotelValidatorFilter filter){
        return bookingMapper.bookingListToResponseList(bookingService.findAll(filter));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public BookingResponse save(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestBody @Valid UpsertBookingRequest request){
        return bookingMapper.bookingToResponse(bookingService.save(
                bookingMapper.requestToBooking(userDetails.getUsername(),request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Integer id,@RequestBody @Valid UpsertBookingRequest request){
        return bookingMapper.bookingToResponse(bookingService.update(bookingMapper.requestToBooking(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        bookingService.deleteById(id);
    }

}
