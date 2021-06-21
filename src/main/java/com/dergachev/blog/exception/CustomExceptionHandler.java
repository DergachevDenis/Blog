package com.dergachev.blog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResponseNotFoundException(NotFoundException exception) {
        ExceptionResponse responseException = ExceptionResponse
                .builder()
                .nameClass(exception.getClass().getName())
                .message(exception.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now().toString())
                .build();
        log.error("IN handleResponseArticleException - {}, {}", exception.getClass().getName(),exception.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ExceptionResponse> handleResponseException(RuntimeException exception) {
        ExceptionResponse responseException = ExceptionResponse
                .builder()
                .nameClass(exception.getClass().getName())
                .message(exception.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now().toString())
                .build();
        log.error("IN handleResponseException - {}, {}", exception.getClass().getName(),exception.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
