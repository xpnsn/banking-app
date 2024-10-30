package com.safevault.accounts.dto;

import com.safevault.accounts.model.AccountType;

import java.time.LocalDateTime;

public class AccountDto {
    private Long accountId;
    private String username;
    private Long mobileNumber;
    private AccountType accountType;
    private double balance;
    private LocalDateTime creationDate;
    private String accountHolderName;
}
