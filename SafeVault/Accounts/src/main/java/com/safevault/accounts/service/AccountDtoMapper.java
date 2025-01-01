package com.safevault.accounts.service;

import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.model.Account;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountDtoMapper implements Function<Account, AccountDto> {
    @Override
    public AccountDto apply(Account account) {
        return new AccountDto(
                account.getAccountId(),
                account.getAccountType(),
                account.getBalance(),
                account.getAccountHolderName()
        );
    }
}
