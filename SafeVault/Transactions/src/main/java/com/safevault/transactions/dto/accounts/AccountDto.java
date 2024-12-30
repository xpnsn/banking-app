package com.safevault.transactions.dto.accounts;

import com.safevault.transactions.model.AccountType;

public record AccountDto (
        Long accountId,
        String username,
        Long mobileNumber,
        AccountType accountType,
        Double balance,
        String accountHolderName
) {}
