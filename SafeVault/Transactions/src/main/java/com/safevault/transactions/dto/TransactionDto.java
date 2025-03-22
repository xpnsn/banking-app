package com.safevault.transactions.dto;

import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;

import java.time.LocalDateTime;

public record TransactionDto(
        String accountTo,
        String accountFrom,
        Double amount,
        LocalDateTime timeStamp,
        TransactionType transactionType,
        TransactionStatus transactionStatus
) {}
