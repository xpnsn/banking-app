package com.safevault.transactions.dto;

import com.safevault.transactions.model.TransactionType;

public record TransactionRequest(
        Long accountFrom,
        Long accountTo,
        String pin,
        Double amount,
        TransactionType transactionType
) {}
