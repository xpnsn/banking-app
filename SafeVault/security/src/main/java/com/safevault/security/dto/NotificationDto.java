package com.safevault.security.dto;

public record NotificationDto(
        String sender,
        String type,
        String message,
        int priority
) {}
