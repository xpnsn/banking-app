package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreationRequest(

        @NotEmpty(message = "The pin must not be empty")
        @Pattern(regexp = "[0-9]+", message = "The pin must only contain numerical values")
        @Size(min = 6, max = 6, message = "The pin must be of 6 digits")
        String pin,

        @NotEmpty(message = "The account type must not be empty")
        @Pattern(regexp = "[a-zA-z]+", message = "The account type must contain only alphabets")
        String accountType
) {}
