package com.example.hotelbookingapplication.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {

    private Integer id;

    private String username;

    private String email;

    private String password;

    @Builder.Default
    private List<AuthorityResponse> roles = new ArrayList<>();
}
