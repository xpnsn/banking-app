package com.safevault.accounts.service;

import com.safevault.accounts.dto.*;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountNotFoundException;

public interface AccountService {

    public boolean userExists(String username);
    public ResponseEntity<?> getAccountById(Long id);
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest);
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) throws AccountNotFoundException;
    public ResponseEntity<?> creditAccount(CreditDebitRequest request);
    public ResponseEntity<?> debitAccount(CreditDebitRequest request);
    public ResponseEntity<?> transfer(TransferRequest request);
}
