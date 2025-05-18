package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record HotelEstimateRequest(

        @NotBlank(message = "Не указан отель который собираетесь оценить")
        String name,

        @NotNull(message = "Вы не поставили оценку")
        @DecimalMin(value = "1.0", message = "Минимальная оценка - {value}")
        @DecimalMax(value = "5.0", message = "Максимальная оценка - {value}")
        @Digits(integer = 1, fraction = 1, message = "Допустимый формат: от 1.0 до 5.0 с одной цифрой после точки")
        BigDecimal newMark
) {

}
