package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllHotelResponse {

    @Builder.Default
    private List<HotelResponse> hotels = new ArrayList<>();
}
