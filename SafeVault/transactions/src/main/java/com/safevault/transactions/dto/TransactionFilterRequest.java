package com.safevault.transactions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record TransactionFilterRequest(

        @NotEmpty(message = "The account cannot be empty")
        @Pattern(regexp = "[0-9]+")
        String id,

        @PastOrPresent(message = "The startDate cannot be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @PastOrPresent(message = "The startDate cannot be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate
) {
}
