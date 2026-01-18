package com.safevault.accounts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.accounts.dto.*;
import com.safevault.accounts.dto.transactions.TransactionDto;
import com.safevault.accounts.exception.*;
import com.safevault.accounts.feignClient.SecurityFeignClient;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.model.AccountStatus;
import com.safevault.accounts.model.AccountType;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;
    private final SecurityFeignClient securityClient;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper, SecurityFeignClient securityClient, ObjectMapper objectMapper, KafkaProducer kafkaProducer) {
        this.repository = repository;
        this.mapper = mapper;
        this.securityClient = securityClient;
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public boolean accountExist(Long accountId) {
        return repository.findById(accountId).orElse(null) != null;
    }

    @Override
    public ResponseEntity<?> getAccountById(Long id) {
        try {
            UserDto user = objectMapper.convertValue(securityClient.validate().getBody(), UserDto.class);
            if(!user.accounts().contains(id)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
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
            Account acc = repository.findByUserIdAndAccountType(Long.valueOf(userId), AccountType.valueOf(accountCreationRequest.accountType())).orElse(null);
            if(acc != null) {
                if(acc.isVerified()) {
                    throw new DuplicateAccountException("You already have an active account of this type.");
                } else {
                    throw new DuplicateAccountException("Your Account is pending approval, You'll be able to access the account once your account is approved!");
                }
            }
            UserDto user = objectMapper.convertValue(securityClient.validate().getBody(), UserDto.class);

            account = new Account(
                    user.name(),
                    AccountType.valueOf(accountCreationRequest.accountType()),
                    Long.valueOf(userId),
                    accountCreationRequest.pin()
            );
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            ResponseEntity<?> responseEntity = securityClient.addAccountToUser(accountDto.accountId().toString());
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                String phoneNumber = objectMapper.convertValue(securityClient.getPhoneNumber(account.getUserId().toString()).getBody(), MessageResponse.class).value();
                kafkaProducer.send(phoneNumber, "SMS", Collections.emptyMap(), 3);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                repository.deleteById(accountDto.userId());
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
        try {
            Account account = repository.findById(accountDeletionRequest.accountId()).orElseThrow(AccountNotFoundException::new);
            if(!Objects.equals(account.getPin(), accountDeletionRequest.pin())) {
                return new ResponseEntity<>("Invalid Pin", HttpStatus.BAD_REQUEST);
            }
            if(account.getBalance() > 1) {
                return new ResponseEntity<>("You still have some funds in this account, try moving the funds before deleting the account!", HttpStatus.BAD_REQUEST);
            }
            securityClient.removeAccountFromUser(accountDeletionRequest.accountId().toString(), accountDeletionRequest.password());
            repository.deleteById(accountDeletionRequest.accountId());

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            verifyInfo(account, account, request.pin(), request.amount());
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
        verifyInfo(accountFrom, accountTo, request.pin(), request.amount());
        accountTo.setBalance(accountTo.getBalance() + request.amount());
        repository.save(accountFrom);
        repository.save(accountTo);

        return new ResponseEntity<>(accountFrom, HttpStatus.OK);
    }

    private void verifyInfo(Account accountFrom, Account accountTo, String pin, Double amount) {
        notInactiveAccount(accountTo);
        if(!accountFrom.getPin().equals(pin)) {
            throw new IncorrectPinException();
        }
        if(accountFrom.getBalance() < amount) {
            throw new InsufficientBalanceException();
        }
        accountFrom.setBalance(accountFrom.getBalance() - amount);
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
        Account account = repository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        UserDto userDto = objectMapper.convertValue(securityClient.validate().getBody(), UserDto.class);
        account.setVerified(true);
        account.setStatus(AccountStatus.ACTIVE);
        repository.save(account);
        kafkaProducer.send(userDto.phoneNumber(), "SMS", Map.of("id", accountId.toString()), 3);
        kafkaProducer.send(userDto.email(), "EMAIL", Map.of("user", account.getAccountHolderName()), 3);
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
    public ResponseEntity<?> addTransactionToAccount(TransactionDto transactionDto) {
        Account accountFrom = repository.findById(Long.valueOf(transactionDto.accountFrom())).orElseThrow(AccountNotFoundException::new);
        Account accountTo = repository.findById(Long.valueOf(transactionDto.accountTo())).orElseThrow(AccountNotFoundException::new);
        accountFrom.getTransactionIds().add(transactionDto.id());
        accountTo.getTransactionIds().add(transactionDto.id());
        MessageResponse debitPhoneNumber = objectMapper.convertValue(securityClient.getPhoneNumber(accountFrom.getUserId().toString()).getBody(), MessageResponse.class);
        MessageResponse creditPhoneNumber = objectMapper.convertValue(securityClient.getPhoneNumber(accountTo.getUserId().toString()).getBody(), MessageResponse.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = transactionDto.timeStamp().format(formatter);
        kafkaProducer.send(creditPhoneNumber.value(), "SMS", Map.of(
                "amount", transactionDto.amount().toString(),
                "accountTo", transactionDto.accountTo(),
                "accountFrom", transactionDto.accountFrom(),
                "date", date,
                "transactionId", transactionDto.id().toString()
                ), 1);
        kafkaProducer.send(debitPhoneNumber.value(), "SMS", Map.of(
                "amount", transactionDto.amount().toString(),
                "accountTo", transactionDto.accountTo(),
                "accountFrom", transactionDto.accountFrom(),
                "date", date,
                "transactionId", transactionDto.id().toString()
        ), 1);

        repository.save(accountTo);
        repository.save(accountFrom);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
