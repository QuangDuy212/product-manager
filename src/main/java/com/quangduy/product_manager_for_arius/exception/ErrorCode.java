package com.quangduy.product_manager_for_arius.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(400, "Uncategorized error", HttpStatus.BAD_REQUEST),
    COOKIES_EMPTY(400, "You don't have refresh_token in cookies", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN(400, "Invalid refresh_token", HttpStatus.BAD_REQUEST),
    INVALID_ACCESSTOKEN(400, "Invalid access token", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(400, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(400, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(400, "This category not found", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(400, "This category is existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(400, "This product not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(400, "This order not found", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
