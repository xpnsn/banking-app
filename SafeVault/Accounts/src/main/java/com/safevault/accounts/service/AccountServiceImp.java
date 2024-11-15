package com.safevault.accounts.service;

import com.safevault.accounts.dto.AccountCreationRequest;
import com.safevault.accounts.dto.AccountDeletionRequest;
import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.exception.InsufficientBalanceException;
import com.safevault.accounts.exception.UserAccountNotFoundException;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean userExists(String username) {
        return repository.findByUsername(username) != null;
    }

    @Override
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest) {
        if(userExists(accountCreationRequest.username())) {
            return new ResponseEntity<>("User already exist with username or mobile number", HttpStatus.BAD_REQUEST);
        }
        Account account = new Account(
                accountCreationRequest.mobileNumber(),
                accountCreationRequest.username(),
                accountCreationRequest.accountHolderName(),
                accountCreationRequest.accountType(),
                accountCreationRequest.password()
        );
        repository.save(account);
        AccountDto accountDto = mapper.apply(account);
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) throws UserAccountNotFoundException {
        try {
            if(!userExists(accountDeletionRequest.username())) {
                throw new UserAccountNotFoundException();
            }
            repository.deleteById(repository.findByUsername(accountDeletionRequest.username()).getAccountId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> creditAccount(Long accountId, Double amount) {
        try {
            Account account = repository.findById(accountId).orElseThrow(UserAccountNotFoundException::new);
            account.setBalance(account.getBalance() + amount);
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> debitAccount(Long accountId, Double amount){
        try {
            Account account = repository.findById(accountId).orElseThrow(UserAccountNotFoundException::new);
            if(account.getBalance() < amount) {
                throw new InsufficientBalanceException();
            }
            account.setBalance(account.getBalance() - amount);
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
