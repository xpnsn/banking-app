package com.safevault.accounts.dto;

import java.util.List;

public record UserDto(
    Long id,
    String username,
    String phoneNumber,
    String name,
    String email,
    List<String> roles,
    List<Long> accounts,
    boolean isVerified
){}
