package com.dergachev.blog.exception;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@Builder
@ToString
public class ExceptionResponse {
    private final String message;
}
