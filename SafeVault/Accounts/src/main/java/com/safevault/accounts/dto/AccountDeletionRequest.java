package com.safevault.accounts.dto;

public record AccountDeletionRequest(
    Long userId, String password
) {}
