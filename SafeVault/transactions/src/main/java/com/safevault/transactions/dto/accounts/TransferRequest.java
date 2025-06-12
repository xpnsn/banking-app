package com.safevault.transactions.dto.accounts;

import com.safevault.transactions.model.TransactionType;

public record TransferRequest(
        Long accountFrom,
        Long accountTo,
        String pin,
        Double amount,
        TransactionType transferType
) {}
