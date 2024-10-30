package com.safevault.accounts.controller;

import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.service.AccountServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountServiceImp service;

    public AccountController(AccountServiceImp service) {
        this.service = service;
    }

    @PostMapping("create")
    public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto) {

        if(service.userExists(accountDto)) {
            return new ResponseEntity<>("User already exist", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("test")
    public ResponseEntity<?> test() {
        return new ResponseEntity<>("test passed", HttpStatus.OK);
    }


}
