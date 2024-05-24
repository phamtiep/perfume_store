package com.backend.java.perfume.dto.auth;

import com.backend.java.perfume.dto.user.UserResponse;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private UserResponse user;
    private String token;
}
