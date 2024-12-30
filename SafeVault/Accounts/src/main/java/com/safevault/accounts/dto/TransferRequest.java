package com.safevault.accounts.dto;

import com.safevault.accounts.model.TransactionType;

public record TransferRequest(
        Long accountFrom,
        Long accountTo,
        String pin,
        Double amount,
        TransactionType transferType
) {}
