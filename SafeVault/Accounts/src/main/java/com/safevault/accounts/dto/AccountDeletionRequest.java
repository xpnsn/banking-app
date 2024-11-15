package com.safevault.accounts.dto;

public record AccountDeletionRequest(
        String username,
        Long mobileNumber,
        String password
) {}
