package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpsertHotelRequest {

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

    @NotBlank(message = "Поле title не может быть пустым")
    private String title;

    @NotBlank(message = "Поле cite не может быть пустым")
    private String city;

    @NotBlank(message = "Поле address не может быть пустым")
    private String address;

    @NotNull(message = "Поле distanceFromCenterCity не может быть пустым")
    private Float distanceFromCenterCity;
}
