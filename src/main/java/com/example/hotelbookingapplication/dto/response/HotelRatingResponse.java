package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HotelRatingResponse {

    private String name;

    private BigDecimal rating;

    private Integer numberOfRating;
}
