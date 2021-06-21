package com.dergachev.blog.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ExceptionResponse {
    private final String nameClass;
    private final String message;
    private final HttpStatus httpStatus;
    private final String timestamp;
}
