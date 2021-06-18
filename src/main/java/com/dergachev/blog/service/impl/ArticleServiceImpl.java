package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.service.ArticleService;
import com.dergachev.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public void editArticle(ArticleRequest request, String email) {

    }

    @Override
    public void addArticle(ArticleRequest request, String email) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setText(request.getText());
        ArticleStatus status = request.getStatus().equalsIgnoreCase("PUBLIC") ? ArticleStatus.PUBLIC : ArticleStatus.PRIVATE;
        article.setStatus(status);
        article.setAuthor_id(userService.findByEmail(email).getId());
        article.setCreated_at(LocalDate.now());
        article.setUpdate_at(LocalDate.now());
        articleRepository.save(article);
    }

    @Override
    public List<Article> getPublicArticles() {
        return null;
    }

    @Override
    public List<Article> getMyArticles(String email) {
        return null;
    }

    @Override
    public void deleteArticle(ArticleRequest request, String email) {

    }
}
