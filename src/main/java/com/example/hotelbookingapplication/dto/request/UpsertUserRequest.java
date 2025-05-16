package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpsertUserRequest {

    @NotBlank(message = "Поле username не может быть пустым")
    @Size(min = 3, max = 30, message = "Имя пользователя не может быть меньше {min} и больше {max}!")
    private String username;

    @NotBlank(message = "Поле email не может быть пустым")
    @Size(min = 3, max = 30, message = "email не может быть меньше {min} и больше {max}!")
    private String email;

    @NotBlank(message = "Поле password не может быть пустым")
    private String password;
}
