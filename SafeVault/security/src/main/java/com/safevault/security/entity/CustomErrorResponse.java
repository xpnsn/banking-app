package com.safevault.security.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
    private String title;
    private int status;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public CustomErrorResponse(String title, Map<String, String> errors) {
        this.title = title;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
    }
}

