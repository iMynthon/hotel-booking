package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpsertBookingRequest {

    @NotBlank(message = "Поле arrivalDate не может быть пустым - формат даты 15.06.1990")
    private String arrivalDate;

    @NotBlank(message = "Поле departureDate не может быть пустым - формат даты 15.06.1990")
    private String departureDate;

    @NotBlank(message = "Поле roomNumber не может быть пустым")
    private Integer roomNumber;
}
