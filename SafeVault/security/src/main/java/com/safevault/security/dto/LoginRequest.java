package com.safevault.security.dto;

public record LoginRequest(
        String username,
        String password
) {}
