package com.safevault.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountDetailRequest(
        @NotEmpty(message = "The account id to be deleted must not be empty")
        @Pattern(regexp = "[0-9]+", message = "The account id must contains only numerical values")
        Long accountId,

        @NotEmpty(message = "The pin must not be empty")
        @Pattern(regexp = "[0-9]+", message = "The pin must contain only numerical values")
        @Size(min = 6, max = 6, message = "The pin must be of 6 digits")
        String pin
) {}
