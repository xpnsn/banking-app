package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

public record AccountCreationRequest(
        String username,
        String password,
        Long mobileNumber,
        String accountHolderName,
        AccountType accountType
) {}
