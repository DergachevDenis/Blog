package com.dergachev.blog.dto;

import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.entity.article.Tag;
import com.dergachev.blog.exception.ArticleException;
import lombok.Data;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.constraints.NotBlank;
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
