package com.dergachev.blog.entity.article;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class ArticlePage {
    private int skip = 0;
    private int limit = 10;
    private Sort.Direction sortDirection = Sort.Direction.ASC;
    private String sortBy = "title";

}
