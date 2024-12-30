package com.safevault.accounts.dto;

public record AccountDeletionRequest(
        String username,
        String email,
        Long mobileNumber,
        String password,
        String pin
) {}
