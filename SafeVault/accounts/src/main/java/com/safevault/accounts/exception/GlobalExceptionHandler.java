package com.safevault.accounts.exception;

import com.safevault.accounts.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AccountNotFoundException.class,
            IncorrectPinException.class,
            InsufficientBalanceException.class,
            InactiveAccountException.class,
            SuspendedAccountException.class,
            DuplicateAccountException.class
    })
    public ResponseEntity<CustomErrorResponse> handleCustomBusinessExceptions(RuntimeException ex) {
        HttpStatus status;
        String title;

        if (ex instanceof AccountNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            title = "Account Not Found";
        } else if (ex instanceof IncorrectPinException) {
            status = HttpStatus.BAD_REQUEST;
            title = "Incorrect PIN";
        } else if (ex instanceof InsufficientBalanceException) {
            status = HttpStatus.BAD_REQUEST;
            title = "Insufficient Balance";
        } else if (ex instanceof InactiveAccountException) {
            status = HttpStatus.BAD_REQUEST;
            title = "Inactive Account";
        } else if (ex instanceof SuspendedAccountException) {
            status = HttpStatus.BAD_REQUEST;
            title = "Account Suspended";
        } else if (ex instanceof DuplicateAccountException) {
            status = HttpStatus.CONFLICT;
            title = "Duplicate Account";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            title = "An Error Occurred";
        }

        CustomErrorResponse response = new CustomErrorResponse(
                title,
                status.value(),
                LocalDateTime.now(),
                Map.of("error", ex.getMessage())
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));
        CustomErrorResponse response = new CustomErrorResponse(
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception ex) {
        CustomErrorResponse response = new CustomErrorResponse(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                Map.of("error", ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
