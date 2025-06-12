package com.safevault.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
}
