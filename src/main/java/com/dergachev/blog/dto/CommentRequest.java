package com.dergachev.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;
}
