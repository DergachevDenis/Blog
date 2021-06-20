package com.dergachev.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ExceptionResponse> handleResponseException(RuntimeException exception) {
        ExceptionResponse responseException = new ExceptionResponse(exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now().toString()
        );
        return new ResponseEntity<>(responseException,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
