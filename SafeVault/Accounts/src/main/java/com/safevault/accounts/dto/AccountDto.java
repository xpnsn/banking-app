package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

import java.time.LocalDateTime;

public record AccountDto (
        Long accountId,
        AccountType accountType,
        Double balance,
        String accountHolderName
) {}
