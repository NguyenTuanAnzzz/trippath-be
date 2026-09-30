package com.an.pathtrip.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    LocalDateTime expiresAt;
}