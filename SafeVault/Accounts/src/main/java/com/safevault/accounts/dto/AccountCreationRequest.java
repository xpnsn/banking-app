package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

public record AccountCreationRequest(
        String pin,
        String accountHolderName,
        AccountType accountType,
        Long userId
) {}
