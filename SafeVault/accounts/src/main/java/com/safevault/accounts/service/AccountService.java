package com.safevault.accounts.service;

import com.safevault.accounts.dto.*;
import com.safevault.accounts.dto.transactions.TransactionDto;
import com.safevault.accounts.model.Account;
import org.springframework.http.ResponseEntity;

import javax.security.auth.login.AccountNotFoundException;

public interface AccountService {

    public boolean accountExist(Long accountId);
    public ResponseEntity<?> getAccountById(Long id);
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest, String userId);
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) throws AccountNotFoundException;
    public ResponseEntity<?> creditAccount(CreditDebitRequest request, boolean isTypeTransfer);
    public ResponseEntity<?> debitAccount(CreditDebitRequest request);
    public ResponseEntity<?> transfer(TransferRequest request);
    public ResponseEntity<?> test();
    public ResponseEntity<?> getUnverifiedAccounts();
    public ResponseEntity<?> verifyAccount(Long accountId);
    public void notInactiveAccount(Account account);
    public ResponseEntity<?> getAccountsByUserId(String userId);
    ResponseEntity<?> addTransactionToAccount(TransactionDto transactionDto);
}
