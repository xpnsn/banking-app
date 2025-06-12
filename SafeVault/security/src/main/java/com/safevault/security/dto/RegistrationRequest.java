package com.safevault.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
    String username,

    @NotEmpty(message = "The first name must not be empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "The name must only contain alphabets")
    @Size(min = 3, max = 15, message = "The name must be between 3 to 15 character only")
    String firstName,

    @NotEmpty(message = "The last name must not be empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "The name must only contain alphabets")
    @Size(min = 3, max = 15, message = "The name must be between 3 to 15 character only")
    String lastName,

    @NotEmpty(message = "The email must not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Enter a valid mail id")
    @Size(max = 30, message = "The email is too long")
    String email,

    @NotEmpty(message = "The phone number must not be empty")
    @Pattern(regexp = "^[\\d\\s\\-()+]+$", message = "Enter a valid phone number")
    @Size(min = 10, max = 10, message = "Enter a valid phone number")
    String phoneNumber,

    @NotEmpty(message = "The password must not be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).*$", message = "The password must have at least an uppercase, a lowercase, a number and a special character")
    @Size(min = 8, max = 16, message = "The password must be between 8 to 16 characters")
    String password
) {}
