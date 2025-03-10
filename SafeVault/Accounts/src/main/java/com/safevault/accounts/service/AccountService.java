package com.safevault.accounts.service;

import com.safevault.accounts.dto.*;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountNotFoundException;

public interface AccountService {

    public boolean accountExist(Long accountId);
    public ResponseEntity<?> getAccountById(Long id);
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest, String userId);
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) throws AccountNotFoundException;
    public ResponseEntity<?> creditAccount(CreditDebitRequest request);
    public ResponseEntity<?> debitAccount(CreditDebitRequest request);
    public ResponseEntity<?> transfer(TransferRequest request);
    public ResponseEntity<?> test(String userId);
    public ResponseEntity<?> getUnverifiedAccounts();
    public ResponseEntity<?> verifyAccount(Long accountId);
}
