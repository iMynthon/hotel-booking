package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomResponse {

    private Integer id;

    private String name;

    private String description;

    private Integer number;

    private Integer price;

    private Integer numberOfPeople;

    private String hotel;

    @Builder.Default
    private List<String> unavailableDate = new ArrayList<>();
}
