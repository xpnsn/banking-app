package com.safevault.transactions.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "NOTIFICATIONS")
public interface NotificationClient {

    @PostMapping("api/v1/notifications/send/{phoneNumber}/{body}")
    public ResponseEntity<?> sendMessage(@PathVariable String phoneNumber, @PathVariable String body);

}