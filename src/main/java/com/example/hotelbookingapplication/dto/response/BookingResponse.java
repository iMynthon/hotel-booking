package com.example.hotelbookingapplication.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookingResponse {

    private Integer id;

    private String arrivalDate;

    private String departureDate;

    private String roomNumber;
}
