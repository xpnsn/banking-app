package com.safevault.notifications.service;

import com.safevault.notifications.dto.NotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;

@Service
public class KafkaConsumer {

    private final TwilioService twilioService;
    private final EmailService emailService;
    PriorityQueue<NotificationDto> queue = new PriorityQueue<>();

    public KafkaConsumer(TwilioService twilioService, EmailService emailService) {
        this.twilioService = twilioService;
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(NotificationDto notificationDto) {
        queue.offer(notificationDto);
    }

    @Scheduled(fixedDelay = 500)
    private void process() {
        while(!queue.isEmpty()) {
            NotificationDto notificationDto = queue.poll();
            if(notificationDto.getType().equalsIgnoreCase("sms")) {
                twilioService.sendMessage(notificationDto);
            } else if (notificationDto.getType().equalsIgnoreCase("email")) {
                emailService.sendMail(notificationDto);
            }
        }
    }
}
