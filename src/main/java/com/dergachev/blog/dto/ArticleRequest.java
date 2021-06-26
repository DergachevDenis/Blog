package com.dergachev.blog.dto;


import com.dergachev.blog.entity.article.Tag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ArticleRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Text cannot be empty")
    private String text;

    @NotBlank
    private String status;

    private List<Tag> tags;
}
