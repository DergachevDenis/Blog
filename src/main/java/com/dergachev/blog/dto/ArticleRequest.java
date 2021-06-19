package com.dergachev.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ArticleRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Text cannot be empty")
    private String text;

    @NotBlank(message = "Status cannot be empty")
    private String status;
}