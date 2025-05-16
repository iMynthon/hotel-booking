package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AllRoomResponse {

    @Builder.Default
    private List<RoomResponse> rooms = new ArrayList<>();
}
