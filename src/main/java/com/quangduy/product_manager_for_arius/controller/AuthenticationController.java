package com.quangduy.product_manager_for_arius.controller;

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
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login success")
                .result(result)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<UserResponse> register(@Valid @RequestBody UserCreationRequest request) throws AppException {
        return ApiResponse.<UserResponse>builder()
                .message("Register success")
                .result(this.authenticationService.register(request))
                .build();
    }

    @GetMapping("/account")
    ApiResponse<UserResponse> getAccount() {
        return ApiResponse.<UserResponse>builder()
                .message("Get account success")
                .result(this.authenticationService.getAccount())
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout() {
        this.authenticationService.logout();
        return ApiResponse.<Void>builder()
                .message("Logout success")
                .result(null)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "duy") String refresh_token) {
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Logout success")
                .result(this.authenticationService.refreshToken(refresh_token))
                .build();
    }
}
