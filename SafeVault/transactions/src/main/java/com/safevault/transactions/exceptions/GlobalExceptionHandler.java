package com.safevault.transactions.exceptions;

import com.safevault.transactions.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        Object invalidValue = ex.getValue();

        String errorMessage = String.format(
                "Invalid value for parameter '%s': '%s'. Expected type: %s.",
                paramName, invalidValue, requiredType
        );
        CustomErrorResponse response = new CustomErrorResponse(
                "Argument Mismatch",
                Map.of("error", errorMessage)
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransactionType.class)
    public ResponseEntity<?> handleInvalidTransactionType(InvalidTransactionType ex) {
        CustomErrorResponse response = new CustomErrorResponse(
                "Invalid Transaction Type",
                Map.of("error", ex.getMessage())
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
