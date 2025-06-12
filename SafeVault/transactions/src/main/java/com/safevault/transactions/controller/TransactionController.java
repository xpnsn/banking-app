package com.safevault.transactions.controller;

import com.safevault.transactions.dto.TransactionFilterRequest;
import com.safevault.transactions.dto.TransactionRequest;
import com.safevault.transactions.service.TransactionServiceImp;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
        @RequestParam Long id,
        @RequestParam(defaultValue = "all") String type,
        @PageableDefault(size = 5, sort = "timeStamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return serviceImp.getTransactions(id, type, pageable);
    }

    @GetMapping("filter")
    public ResponseEntity<?> getTransactionsFilter(
        @Valid @RequestBody TransactionFilterRequest request,
        @PageableDefault(size = 5, sort = "timeStamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return serviceImp.getTransactionFilter(request, pageable);
    }
}
