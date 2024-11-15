package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

import java.time.LocalDateTime;

public record AccountDto (
        Long accountId,
        String username,
        Long mobileNumber,
        AccountType accountType,
        Double balance,
        String accountHolderName
) {}
