package com.cg.account.exception.handler;

import com.cg.account.exception.ErrorMessage;
import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class LedgerServiceErrorHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return new ResponseEntity<ErrorMessage>(ErrorMessage.builder().errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException runtimeException,WebRequest request) {
        return new ResponseEntity<ErrorMessage>(ErrorMessage.builder().errorMessage(runtimeException.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> handleOtherException(Exception ex,WebRequest request) {
        return new ResponseEntity<ErrorMessage>(ErrorMessage.builder().errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {CommandExecutionException.class})
    public ResponseEntity<ErrorMessage> handleCommandExecutionException(CommandExecutionException commandExecutionException, WebRequest request) {
        return new ResponseEntity<ErrorMessage>(ErrorMessage.builder().errorMessage(commandExecutionException.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}