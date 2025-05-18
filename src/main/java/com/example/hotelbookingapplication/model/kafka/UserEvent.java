package com.example.hotelbookingapplication.model.kafka;

import lombok.*;

import java.time.LocalDateTime;

@Builder
public record UserEvent(
        Integer userId,
        LocalDateTime registrationTime
){
}
