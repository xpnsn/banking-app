package com.safevault.accounts.feignClient;

import com.safevault.accounts.dto.transactions.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "NOTIFICATIONS")
public interface NotificationClient {

    @PostMapping("api/v1/notifications/send/{phoneNumber}/{body}")
    public ResponseEntity<?> sendMessage(@PathVariable String phoneNumber, @PathVariable String body);

    @PostMapping(value = "api/v1/notifications/credit-debit-message")
    public ResponseEntity<?> sendCreditDebitMessages(@RequestParam String creditPhoneNumber, @RequestParam String debitPhoneNumber, @RequestBody TransactionDto transactionDto);
}