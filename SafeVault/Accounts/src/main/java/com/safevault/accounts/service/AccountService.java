package com.safevault.accounts.service;

import com.safevault.accounts.dto.AccountCreationRequest;
import com.safevault.accounts.dto.AccountDeletionRequest;
import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.dto.TransferRequest;
import com.safevault.accounts.model.Account;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountNotFoundException;

public interface AccountService {

    public boolean userExists(String username);
    public AccountDto getAccountById(Long id);
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest);
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) throws AccountNotFoundException;
    public ResponseEntity<?> creditAccount(Long accountId, Double amount);
    public ResponseEntity<?> debitAccount(Long accountId, Double amount);
    public ResponseEntity<?> transfer(TransferRequest request);
}
