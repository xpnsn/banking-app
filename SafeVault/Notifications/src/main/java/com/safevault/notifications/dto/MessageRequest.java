package com.safevault.notifications.dto;

public record MessageRequest(
        String messageBody,
        String contactNumber
) {}
