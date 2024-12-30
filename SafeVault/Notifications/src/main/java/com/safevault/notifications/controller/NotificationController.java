package com.safevault.notifications.controller;

import com.safevault.notifications.dto.MessageRequest;
import com.safevault.notifications.service.NotificationServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {

    @Autowired
    NotificationServiceImp service;

    @PostMapping("sms")
    public ResponseEntity<?> sendNotificationViaSMS(@RequestBody MessageRequest request) {
        return service.notifyViaSms(request);
    }

    @GetMapping
    public String test() {return "success";}
}
