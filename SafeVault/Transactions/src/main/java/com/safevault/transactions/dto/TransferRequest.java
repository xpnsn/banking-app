package com.safevault.transactions.dto;

import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;

public record TransferRequest(
        Long accountFrom,
        Long accountTo,
        Double amount,
        TransactionType transactionType
) {}
