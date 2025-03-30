package com.safevault.accounts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.accounts.dto.*;
import com.safevault.accounts.exception.*;
import com.safevault.accounts.feignClient.SecurityFeignClient;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.model.AccountStatus;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;
    private final SecurityFeignClient securityClient;
    private final ObjectMapper objectMapper;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper, SecurityFeignClient securityClient, ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.securityClient = securityClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean accountExist(Long accountId) {
        return repository.findById(accountId).orElse(null) != null;
    }

    @Override
    public ResponseEntity<?> getAccountById(Long id) {
        try {
            Account account = repository.findById(id).orElseThrow(AccountNotFoundException::new);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest, String userId) {
        Account account = null;
        try {
            Account acc = repository.findByUserIdAndAccountType(Long.valueOf(userId), accountCreationRequest.accountType()).orElse(null);
            if(acc != null) {
                if(acc.isVerified()) {
                    throw new DuplicateAccountException("You already have an active account of this type.");
                } else {
                    throw new DuplicateAccountException("Your request for this account type is already pending approval.");
                }
            }
            UserDto user = objectMapper.convertValue(securityClient.validate().getBody(), UserDto.class);

            account = new Account(
                    user.name(),
                    accountCreationRequest.accountType(),
                    Long.valueOf(userId),
                    accountCreationRequest.pin()
            );
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            ResponseEntity<?> responseEntity = securityClient.addAccountToUser(accountDto.accountId().toString());
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
            } else {
                repository.delete(account);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (IllegalArgumentException e) {
            if(account != null) {
                repository.delete(account);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) {
//        try {
//            if(!accountExist(accountDeletionRequest.userId())) {
//                throw new AccountNotFoundException();
//            }
//            repository.deleteById(accountDeletionRequest.userId());
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        return new ResponseEntity<>("Account Removed!", HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<?> creditAccount(CreditDebitRequest request, boolean isTypeTransfer) {
        try {
            Account account = repository.findById(request.accountId()).orElseThrow(AccountNotFoundException::new);
            notInactiveAccount(account);
            if(!account.getPin().equals(request.pin()) && !isTypeTransfer) {
                throw new IncorrectPinException();
            }
            account.setBalance(account.getBalance() + request.amount());
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> debitAccount(CreditDebitRequest request){
        try {
            Account account = repository.findById(request.accountId()).orElseThrow(AccountNotFoundException::new);
            notInactiveAccount(account);
            if(!account.getPin().equals(request.pin())) {
                throw new IncorrectPinException();
            }
            if(account.getBalance() < request.amount()) {
                throw new InsufficientBalanceException();
            }
            account.setBalance(account.getBalance() - request.amount());
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> transfer(TransferRequest request) {
        Account accountFrom = repository.findById(request.accountFrom()).orElseThrow(AccountNotFoundException::new);
        Account accountTo = repository.findById(request.accountTo()).orElseThrow(AccountNotFoundException::new);
        notInactiveAccount(accountFrom);
        notInactiveAccount(accountTo);
        if(!accountFrom.getPin().equals(request.pin())) {
            throw new IncorrectPinException();
        }
        if(accountFrom.getBalance() < request.amount()) {
            throw new InsufficientBalanceException();
        }
        accountFrom.setBalance(accountFrom.getBalance() - request.amount());
        accountTo.setBalance(accountTo.getBalance() + request.amount());
        repository.save(accountFrom);
        repository.save(accountTo);
        return new ResponseEntity<>(accountFrom, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> test() {
        return new ResponseEntity<>(securityClient.validate().getBody(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getUnverifiedAccounts() {
        List<Account> unverifiedAccounts = repository.findAll().stream().filter(account -> !account.isVerified()).toList();
        return new ResponseEntity<>(unverifiedAccounts, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> verifyAccount(Long accountId) {
        Account account = repository.findById(accountId).orElse(null);
        if(account == null) {
            throw new AccountNotFoundException();
        }
        account.setVerified(true);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @Override
    public void notInactiveAccount(Account account) {
        if(account.getStatus().equals(AccountStatus.INACTIVE)) {
            throw new InactiveAccountException();
        }
    }

    @Override
    public ResponseEntity<?> getAccountsByUserId(String userId) {
        List<Account> allByUserId = repository.getAllByUserId(Long.valueOf(userId));
        return new ResponseEntity<>(allByUserId, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addTransactionToAccount(AddTransactionRequest request) {
        Account account = repository.findById(request.accountId()).orElseThrow(AccountNotFoundException::new);
        account.getTransactionIds().add(request.TransactionId());
        repository.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
