package com.dergachev.blog.service;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;

import java.util.List;

public interface ArticleService {
    void editArticle(ArticleRequest request, Integer id_article, String email);
    void addArticle(ArticleRequest request, String email);
    List<Article> getPublicArticles();
    List<Article> getArticles(Integer skip, Integer limit, String title, Integer authorId, String sort);
    List<Article> getMyArticles(String email);
    void deleteArticle(Integer id_article, String email);

}
