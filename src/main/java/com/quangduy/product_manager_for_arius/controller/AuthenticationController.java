package com.quangduy.product_manager_for_arius.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.AuthenticationRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.AuthenticationResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.service.AuthenticationService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    @ApiMessage("Login success")
    ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/register")
    @ApiMessage("Register success")
    ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreationRequest request) throws AppException {
        return this.authenticationService.register(request);
    }

    @GetMapping("/account")
    @ApiMessage("Get account success")
    ResponseEntity<UserResponse> getAccount() {
        return this.authenticationService.getAccount();
    }

    @PostMapping("/logout")
    @ApiMessage("Logout success")
    ResponseEntity<Void> logout() {
        return this.authenticationService.logout();
    }

    @PostMapping("/refresh")
    @ApiMessage("Logout success")
    ResponseEntity<AuthenticationResponse> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "duy") String refresh_token) {
        return this.authenticationService.refreshToken(refresh_token);
    }
}
