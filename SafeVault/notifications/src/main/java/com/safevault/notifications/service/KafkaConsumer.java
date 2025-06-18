package com.safevault.notifications.service;

import com.safevault.notifications.dto.NotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;

@Service
public class KafkaConsumer {

    private final TwilioService twilioService;
    PriorityQueue<NotificationDto> queue = new PriorityQueue<>();

    public KafkaConsumer(TwilioService twilioService) {
        this.twilioService = twilioService;
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
//            System.out.println("processing messages...");
            twilioService.sendMessage(notificationDto.getSender(), notificationDto.getMessage());
//            System.out.println("messages processed");
        }
    }
}
