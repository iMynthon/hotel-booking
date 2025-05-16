package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record HotelEstimateRequest(

        @NotBlank(message = "Не указан отель который собираетесь оценить")
        String name,

        @NotNull(message = "Вы не поставили оценку")
        @Size(min = 1, max = 5, message = "Минимальная оценка - {min}, максимальная - {max}")
        BigDecimal newMark
) {

}
