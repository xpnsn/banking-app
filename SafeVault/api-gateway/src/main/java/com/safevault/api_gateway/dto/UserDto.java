package com.safevault.api_gateway.dto;

import java.util.List;

public record UserDto(
    Long id,
    String username,
    String phoneNumber,
    String name,
    String email,
    List<String> roles,
    List<Long> accounts
) {
}
