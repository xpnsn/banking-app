package com.safevault.notifications.service;

import com.safevault.notifications.dto.NotificationDto;
import com.safevault.notifications.dto.transations.TransactionDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class TwilioService {

    @Value("${twilio.ssid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Value("${twilio.phone}")
    private String phoneNumber;

    public TwilioService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    private final ResourceLoader resourceLoader;

    public void sendMessage(NotificationDto notificationDto) {

        String body = "";
        try{
            Resource resource = resourceLoader.getResource("classpath:templates/sms/"+notificationDto.getData().get("type")+".txt");
            body = Files.readString(resource.getFile().toPath());
            for(Map.Entry<String, String> entry : notificationDto.getData().entrySet()) {
                body = body.replace("{{"+entry.getKey()+"}}", entry.getValue());
            }
            Message message = Message.
                    creator(
                            new com.twilio.type.PhoneNumber(notificationDto.getSender()),
                            new com.twilio.type.PhoneNumber(phoneNumber),
                            body
                    ).create();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: "+notificationDto.getData().get("type"), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<?> sendCreditDebitMessages(TransactionDto transactionDto, String creditPhoneNumber, String debitPhoneNumber) {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        String date = transactionDto.timeStamp().format(formatter);
//        String creditMessage = "Rs."+transactionDto.amount()+" credited to a/c "+transactionDto.accountTo()+" on "+date+"  by a/c "+transactionDto.accountFrom()+". Transaction id:"+transactionDto.id();
//        String debitMessage = "A/c "+transactionDto.accountFrom()+" debited Rs."+transactionDto.amount()+" on "+date+" to "+transactionDto.accountTo()+". Transaction id:"+transactionDto.id();
//        sendMessage(creditPhoneNumber, creditMessage);
//        sendMessage(debitPhoneNumber, debitMessage);
        return ResponseEntity.ok().build();
    }
}
