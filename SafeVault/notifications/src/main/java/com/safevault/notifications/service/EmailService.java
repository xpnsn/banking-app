package com.safevault.notifications.service;

import com.safevault.notifications.dto.NotificationDto;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    public EmailService(JavaMailSender mailSender, ResourceLoader resourceLoader) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }

    public void sendMail(NotificationDto notificationDto) {

        String body = "";
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/email/" + notificationDto.getData().get("type") + ".html");
            body = Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + notificationDto.getData().get("type"), e);
        }
        for (Map.Entry<String, String> entry : notificationDto.getData().entrySet()) {
            body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        MimeMessage mimeMessage = null;
        try {
            mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setRecipients(Message.RecipientType.TO, notificationDto.getSender());
            mimeMessage.setSubject(notificationDto.getData().get("subject"));
            mimeMessage.setContent(body, "text/html; charset=utf-8");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(mimeMessage);
    }
}
