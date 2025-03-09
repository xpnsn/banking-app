package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

public record AccountDto (
        Long accountId,
        AccountType accountType,
        Double balance,
        String accountHolderName,
        Long userId
) {}
