package com.safevault.notifications.controller;

import com.safevault.notifications.dto.transations.TransactionDto;
import com.safevault.notifications.service.TwilioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
public class TwilioController {


    private final TwilioService service;

    public TwilioController(TwilioService service) {
        this.service = service;
    }

    @PostMapping("send/{phoneNumber}/{body}")
    public ResponseEntity<?> sendMessage(@PathVariable String phoneNumber, @PathVariable String body) {
        return service.sendMessage(phoneNumber, body);
    }

    @PostMapping("credit-debit-message")
    public ResponseEntity<?> sendCreditDebitMessages(@RequestParam String creditPhoneNumber, @RequestParam String debitPhoneNumber, @RequestBody TransactionDto transactionDto) {
        return service.sendCreditDebitMessages(transactionDto, creditPhoneNumber, debitPhoneNumber);
    }
    @GetMapping("test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("TEST");
    }
}
