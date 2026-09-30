package com.an.pathtrip.controllers;

import com.an.pathtrip.dtos.request.UpdateUserRequest;
import com.an.pathtrip.dtos.response.UserResponse;
import com.an.pathtrip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/me")
    public UserResponse getMe(Authentication authentication) {

        return service.getMe(authentication);
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse updateMe(Authentication authentication, @ModelAttribute @Valid UpdateUserRequest request) {

        return service.updateMe(authentication, request);
    }
}
