package com.dergachev.blog.service;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;

import java.util.List;

public interface ArticleService {
    void editArticle(ArticleRequest request, String email);
    void addArticle(ArticleRequest request, String email);
    List<Article> getPublicArticles();
    List<Article> getMyArticles(String email);
    void deleteArticle(ArticleRequest request, String email);
}
