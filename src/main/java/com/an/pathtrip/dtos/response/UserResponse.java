package com.an.pathtrip.dtos.response;

import com.an.pathtrip.enums.Role;
import com.an.pathtrip.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private Role role;
    private UserStatus status;
}