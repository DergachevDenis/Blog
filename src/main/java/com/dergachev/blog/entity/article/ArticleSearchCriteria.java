package com.dergachev.blog.entity.article;

import lombok.Data;

import java.util.List;

@Data
public class ArticleSearchCriteria {
    private String title;
    private Integer authorId;
    private List<String> tags;
}
