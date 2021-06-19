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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArticleServiceImpl(UserService userService, ArticleRepository articleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

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
        article.setStatus(getArticleStatus(request));
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        articleRepository.save(article);
    }

    @Override
    public void addArticle(ArticleRequest request, String email) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setText(request.getText());
        article.setStatus(getArticleStatus(request));
        article.setAuthorId(userService.findByEmail(email).getId());
        article.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        articleRepository.save(article);
    }

    @Override
    public List<Article> getPublicArticles() {
        return articleRepository.findAllByStatus(ArticleStatus.PUBLIC);
    }

    @Override
    public List<Article> getArticles(Integer skip, Integer limit, String title, Integer authorId, String sort) {
        if (title == null && authorId == null) {
            return articleRepository.findAll(PageRequest.of(skip, skip + limit, Sort.by(sort))).getContent();
        } else if (title == null) {
            return articleRepository.findAllByAuthorId(authorId, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        } else if (authorId == null) {
            return articleRepository.findAllByTitle(title, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        } else {
            return articleRepository.findByTitleAndAuthorId(title, authorId, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        }
    }

    @Override
    public List<Article> getMyArticles(String email_user) {
        Integer author_id = userRepository.findByEmail(email_user).getId();
        return articleRepository.findAllByAuthorId(author_id);
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

    private ArticleStatus getArticleStatus(ArticleRequest request) {
        return request.getStatus().equalsIgnoreCase("PUBLIC") ? ArticleStatus.PUBLIC : ArticleStatus.PRIVATE;
    }
}
