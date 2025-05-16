package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private Integer id;

    private String name;

    private String title;

    private String city;

    private String address;

    private Double distanceFromCenterCity;

    private BigDecimal rating;

    private Integer numberOfRating;

    @Builder.Default
    private List<RoomResponse> rooms = new ArrayList<>();
}
