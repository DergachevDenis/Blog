package com.dergachev.blog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResponseNotFoundException(NotFoundException exception) {
        ExceptionResponse responseException = ExceptionResponse
                .builder()
                .message("Resource not found")
                .build();
        log.error("IN handleResponseArticleException - {}, {}", exception.getClass().getName(),exception.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponse> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        ExceptionResponse responseException = ExceptionResponse
                .builder()
                .message("Invalid body")
                .build();
        log.error("IN handleMessageNotReadableException - {}, {}", exception.getClass().getName(),exception.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ExceptionResponse> handleResponseException(RuntimeException exception) {
        ExceptionResponse responseException = ExceptionResponse
                .builder()
                .message("INTERNAL SERVER ERROR")
                .build();
        log.error("IN handleResponseException - {}, {}", exception.getClass().getName(),exception.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
