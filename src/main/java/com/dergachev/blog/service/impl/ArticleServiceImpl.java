package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.ArticleService;
import com.dergachev.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void editArticle(ArticleRequest request, Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new ArticleException(String.format("Article with id: %s not found", id_article)));

        if (!article.getAuthorId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }

        article.setTitle(request.getTitle());
        article.setText(request.getText());
        article.setText(request.getText());
        ArticleStatus status = request.getStatus().equalsIgnoreCase("PUBLIC") ? ArticleStatus.PUBLIC : ArticleStatus.PRIVATE;
        article.setStatus(status);
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        articleRepository.save(article);
    }

    @Override
    public void addArticle(ArticleRequest request, String email) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setText(request.getText());
        ArticleStatus status = request.getStatus().equalsIgnoreCase("PUBLIC") ? ArticleStatus.PUBLIC : ArticleStatus.PRIVATE;
        article.setStatus(status);
        article.setAuthorId(userService.findByEmail(email).getId());
        article.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        articleRepository.save(article);
    }

    @Override
    public List<Article> getPublicArticles() {
        // List<Article> articles = articleRepository.findByTitle(post_title, PageRequest.of(skip, skip + limit, Sort.by(post_title)));
        List<Article> articles = articleRepository.findAllByStatus(ArticleStatus.PUBLIC);
        return articles;
    }

    @Override
    public List<Article> getMyArticles(String email_user) {
        Integer author_id = userRepository.findByEmail(email_user).getId();
        List<Article> articles = articleRepository.findAllByAuthorId(author_id);
        return articles;
    }

    @Override
    public void deleteArticle(Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new ArticleException(String.format("Article with id: %s not found", id_article)));

        if (!article.getAuthorId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }
        articleRepository.deleteById(id_article);
    }
}
