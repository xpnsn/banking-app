package com.safevault.accounts.dto;

import java.util.List;

public record UserDto (
        Long id,
        String username,
        String name,
        String email,
        List<String> roles
) {}
