package com.dergachev.blog.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionResponse {
    private final String message;
    private final HttpStatus httpStatus;
    private final String timestamp;
}
