package com.dergachev.blog.service;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;

import java.util.List;
import java.util.Set;

public interface ArticleService {

    void editArticle(ArticleRequest request, Integer id_article, String email);

    void addArticle(ArticleRequest request, String email);

    List<Article> getArticles(Integer skip, Integer limit, String title, Integer authorId, String sort, String order);

    List<Article> getMyArticles(String email);

    void deleteArticle(Integer id_article, String email);

    Set<Article> getArticlesTags(List<String> tags);
}
