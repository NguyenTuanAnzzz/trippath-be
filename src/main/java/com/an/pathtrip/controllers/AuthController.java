package com.an.pathtrip.controllers;

import com.an.pathtrip.dtos.request.LoginRequest;
import com.an.pathtrip.dtos.request.RegisterRequest;
import com.an.pathtrip.dtos.request.VerifyEmailRequest;
import com.an.pathtrip.dtos.response.LoginResponse;
import com.an.pathtrip.dtos.response.RegisterResponse;
import com.an.pathtrip.dtos.response.VerifyEmailResponse;
import com.an.pathtrip.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request){
        return service.register(request);
    }

    @PostMapping("/verify-email")
    public VerifyEmailResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request){
        return service.verifyEmail(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response){
        return service.login(request, response);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        return service.refresh(refreshToken);
    }

}
