package com.safevault.transactions.dto;

import com.safevault.transactions.model.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record TransactionRequest(

        @NotEmpty(message = "The senders account id must not be empty")
        Long accountFrom,

        @NotEmpty(message = "The receivers account id must not be empty")
        Long accountTo,

        @NotEmpty(message = "The pin must not be empty")
        @Pattern(regexp = "[0-9]+", message = "The pin must contain numerical values only")
        String pin,

        @NotEmpty(message = "The amount must not be empty")
        Double amount,

        @NotEmpty(message = "The senders account id must not be empty")
        @Pattern(regexp = "[a-zA-Z]+", message = "Invalid Transaction Type")
        TransactionType transactionType
) {}
