package com.safevault.accounts.dto;

import org.springframework.http.HttpStatus;

public record ApiMessageResponse(String message, int status, String httpStatus) {

    public static ApiMessageResponse of(String message, HttpStatus status) {
        return new ApiMessageResponse(message, status.value(), status.name());
    }
}
