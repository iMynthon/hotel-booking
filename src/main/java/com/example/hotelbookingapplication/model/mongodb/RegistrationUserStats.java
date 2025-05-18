package com.example.hotelbookingapplication.model.mongodb;

import com.example.hotelbookingapplication.model.jpa.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Document(collection = "registered_users_id")
@Builder
public class RegistrationUserStats {
    @Id
    private Integer id;

    private LocalDateTime registeredTime;

    @Transient
    private User user;
}
