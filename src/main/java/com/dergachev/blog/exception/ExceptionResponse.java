package com.dergachev.blog.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class ExceptionResponse {
    private final String message;
    private final HttpStatus httpStatus;
    private final String timestamp;
}
