package com.example.hotelbookingapplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpsertUserRequest {

    @NotBlank(message = "Поле username не может быть пустым")
    private String username;
    @NotBlank(message = "Поле email не может быть пустым")
    private String email;
    @NotBlank(message = "Поле password не может быть пустым")
    private String password;
}
