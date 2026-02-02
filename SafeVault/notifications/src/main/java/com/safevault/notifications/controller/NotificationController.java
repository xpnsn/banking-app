package com.safevault.notifications.controller;

import com.safevault.notifications.dto.NotificationDto;
import com.safevault.notifications.dto.transations.TransactionDto;
import com.safevault.notifications.service.TwilioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {


    private final TwilioService service;

    public NotificationController(TwilioService service) {
        this.service = service;
    }

    @PostMapping("send/sms")
    public ResponseEntity<?> sendMessage(@RequestBody NotificationDto notificationDto) {
        service.sendMessage(notificationDto);
        return ResponseEntity.ok("SENT!");
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
