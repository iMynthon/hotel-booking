package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpsertRoomRequest {


    @NotBlank(message = "Не задано поле: name")
    private String name;

    @NotBlank(message = "Не задано поле: description")
    private String description;

    @NotBlank(message = "Не задано поле: number")
    private Integer number;

    @NotBlank(message = "Не задано поле: numberOfPeople")
    private Integer numberOfPeople;

    @NotEmpty(message = "Пустой список: unavailableRoomInDate")
    @Builder.Default
    private List<LocalDateTime> unavailableRoomInDate = new ArrayList<>();

    @NotBlank(message = "Не задано поле: hotel")
    private String hotel;
}
