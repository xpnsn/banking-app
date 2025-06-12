package com.safevault.notifications.service;

import com.safevault.notifications.dto.transations.TransactionDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class TwilioService {

    @Value("${twilio.ssid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Value("${twilio.phone}")
    private String phoneNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public ResponseEntity<?> sendMessage(String toPhoneNumber, String body) {
        try {
            Message message = Message.
                    creator(
                        new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber(phoneNumber),
                        body
                    ).create();
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<?> sendCreditDebitMessages(TransactionDto transactionDto, String creditPhoneNumber, String debitPhoneNumber) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = transactionDto.timeStamp().format(formatter);
        String creditMessage = "Rs."+transactionDto.amount()+" credited to a/c "+transactionDto.accountTo()+" on "+date+"  by a/c "+transactionDto.accountFrom()+". Transaction id:"+transactionDto.id();
        String debitMessage = "A/c "+transactionDto.accountFrom()+" debited Rs."+transactionDto.amount()+" on "+date+" to "+transactionDto.accountTo()+". Transaction id:"+transactionDto.id();
        sendMessage(creditPhoneNumber, creditMessage);
        sendMessage(debitPhoneNumber, debitMessage);
        return ResponseEntity.ok().build();
    }
}
