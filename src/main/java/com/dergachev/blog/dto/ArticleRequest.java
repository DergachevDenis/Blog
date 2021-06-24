package com.dergachev.blog.dto;

import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.entity.article.Tag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ArticleRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Text cannot be empty")
    private String text;

    private ArticleStatus status;

    private List<Tag> tags;

    public void setStatus(String status) {
        status = status.toUpperCase();
        this.status = ArticleStatus.valueOf(status);
    }
}
