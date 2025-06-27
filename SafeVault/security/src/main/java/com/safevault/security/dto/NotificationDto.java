package com.safevault.security.dto;

import java.util.Map;

public record NotificationDto(
        String sender,
        String type,
        Map<String, String> data,
        int priority
) {}
