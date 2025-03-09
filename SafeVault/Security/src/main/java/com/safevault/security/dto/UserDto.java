package com.safevault.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
public record UserDto(
    Long id,
    String username,
    String name,
    String email,
    List<String> roles
){}
