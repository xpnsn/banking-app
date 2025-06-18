package com.safevault.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotEmpty(message = "The username must not be empty")
        @Pattern(regexp = "^(?=.*[a-z])[a-z0-9_]+$", message = "The username must contain lowercase alphabets, and addition digits and underscore if required")
        @Size(min = 4, max = 12, message = "The username must be between 4 to 12 characters only")
        String username,

        @NotEmpty(message = "The password must not be empty")
//        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).*$", message = "Invalid Password")
        @Size(min = 8, max = 16, message = "The password must be between 8 to 16 characters")
        String password
) {}
