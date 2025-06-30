//package com.safevault.notifications.controller;
//
//import com.safevault.notifications.service.EmailService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/v1/notifications")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    public EmailController(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    @PostMapping("send/email")
//    public ResponseEntity<?> sendEmail(@RequestBody EmailDto emailDto) {
////        return emailService.sendMail(emailDto);
//        return null;
//    }
//}
