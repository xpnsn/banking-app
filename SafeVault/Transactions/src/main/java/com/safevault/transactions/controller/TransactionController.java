package com.safevault.transactions.controller;

import com.safevault.transactions.dto.TransactionRequest;
import com.safevault.transactions.service.TransactionServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {

    private final TransactionServiceImp serviceImp;

    public TransactionController(TransactionServiceImp serviceImp) {
        this.serviceImp = serviceImp;
    }

    @PostMapping("initiate")
    public ResponseEntity<?> initiateTransaction(@RequestBody TransactionRequest request) {
        return serviceImp.initiateTransaction(request);
    }

    @GetMapping({"{id}"})
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        return serviceImp.getAccount(id);
    }
    @GetMapping("test")
    public String test() {return "SUCCESS";}
}
