package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomResponse {

    private String name;

    private String description;

    private Integer number;

    private Integer numberOfPeople;

    private String hotel;

    @Builder.Default
    private List<LocalDateTime> unavailableRoomInDate = new ArrayList<>();
}
