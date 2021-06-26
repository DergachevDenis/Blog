package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.*;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.exception.NotFoundException;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.TagRepository;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.ArticleService;
import com.dergachev.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ArticleServiceImpl(UserService userService, ArticleRepository articleRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void editArticle(ArticleRequest request, Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new NotFoundException(String.format("Article with id: %s not found", id_article)));

        if (!article.getUser().getId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }
        fillArticle(request, email_user, article);
        addTagsToArticle(request, article);
        articleRepository.save(article);
    }

    @Override
    public void addArticle(ArticleRequest request, String email) {
        Article article = new Article();
        fillArticle(request, email, article);
        article.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        addTagsToArticle(request, article);
        articleRepository.save(article);
    }

    @Override
    public List<Article> getArticles(Integer skip, Integer limit, String title, Integer authorId, String sort, String order) {
        if (title == null && authorId == null) {
            return articleRepository.findAllWithFilters(limit, skip, sort, order);
        } else if (title == null) {
            return articleRepository.findAllWithFiltersByAuthorId(limit, skip, sort, order, authorId);
        } else if (authorId == null) {
            return articleRepository.findAllWithFiltersByTitle(limit, skip, sort, order, title);
        } else {
            return articleRepository.findAllWithFiltersByTitleAndAuthorId(limit, skip, sort, order, title, authorId);
        }
    }

    @Override
    public Set<Article> getArticlesTags(List<String> tags) {
        return articleRepository.findArticleByTagList(tags);
    }

    @Override
    public List<Article> getMyArticles(String email_user) {
        Integer authorId = userRepository.findByEmail(email_user).getId();
        return articleRepository.findAllByUserId(authorId);
    }

    @Override
    public void deleteArticle(Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new NotFoundException(String.format("Article with id: %s not found", id_article)));

        if (!article.getUser().getId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }
        articleRepository.deleteById(id_article);
    }

    private void fillArticle(ArticleRequest request, String email, Article article) {
        article.setTitle(request.getTitle());
        article.setText(request.getText());
        article.setStatus(getStatusArticle(request.getStatus()));
        article.setUser(userService.findByEmail(email));
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private ArticleStatus getStatusArticle(String statusDto) {
        ArticleStatus status;
        statusDto = statusDto.toUpperCase();
        try {
           status = ArticleStatus.valueOf(statusDto);
        }
        catch (IllegalArgumentException exception){
            throw new ArticleException("Invalid article status");
        }
        return status;
    }

    private void addTagsToArticle(ArticleRequest request, Article article) {
        List<Tag> tags = request.getTags();
        for (Tag tag : tags) {
            Tag existingTag = tagRepository.findByName(tag.getName());
            if (existingTag == null) {
                tagRepository.save(tag);
                article.getTags().add(tag);
            } else {
                article.getTags().add(existingTag);
            }
        }
    }
}
