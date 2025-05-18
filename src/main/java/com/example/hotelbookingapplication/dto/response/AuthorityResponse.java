package com.example.hotelbookingapplication.dto.response;

import com.example.hotelbookingapplication.model.jpa.RoleType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthorityResponse {

    private RoleType role;
}
