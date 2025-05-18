package com.example.hotelbookingapplication.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "Не задано поле: number")
    private Integer number;

    @NotNull(message = "Не задано поле: price")
    private Integer price;

    @NotNull(message = "Не задано поле: numberOfPeople")
    private Integer numberOfPeople;

    @Builder.Default
    @JsonFormat(pattern = "dd.MM.yyyy")
    private List<String> unavailableDate = new ArrayList<>();

    @NotBlank(message = "Не задано поле: hotel")
    private String hotel;
}
