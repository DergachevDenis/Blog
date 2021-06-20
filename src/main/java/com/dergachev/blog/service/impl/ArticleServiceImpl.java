package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.*;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.repository.ArticleCriteriaRepository;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.TagRepository;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.ArticleService;
import com.dergachev.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final UserService userService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticleCriteriaRepository articleCriteriaRepository;

    @Autowired
    public ArticleServiceImpl(UserService userService, ArticleRepository articleRepository, UserRepository userRepository, TagRepository tagRepository, ArticleCriteriaRepository articleCriteriaRepository) {
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.articleCriteriaRepository = articleCriteriaRepository;
    }

    @Override
    public void editArticle(ArticleRequest request, Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new ArticleException(String.format("Article with id: %s not found", id_article)));

        if (!article.getUser().getId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }
        fillArticle(request, email_user, article);
        fillTagsArticle(request, article);
        articleRepository.save(article);
    }

    @Override
    public void addArticle(ArticleRequest request, String email) {
        System.out.println("ТУТ1");
        Article article = new Article();
        fillArticle(request, email, article);
        article.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fillTagsArticle(request, article);
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
            return articleRepository.findAllByUserId(authorId, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        } else if (authorId == null) {
            return articleRepository.findAllByTitle(title, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        } else {
            return articleRepository.findByTitleAndUserId(title, authorId, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        }
    }

    public Page<Article> getArticlesFilter(Integer skip, Integer limit, String title, Integer authorId, String sort, String sortDir, List<String> tags) {
        ArticlePage articlePage = new ArticlePage();
        if (!Objects.nonNull(skip)) {
            articlePage.setSkip(skip);
        }
        if (!Objects.nonNull(limit)) {
            articlePage.setLimit(limit);
        }
        if (!Objects.nonNull(sort)) {
            articlePage.setSortBy(sort);
        }
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        articlePage.setSortDirection(sortDirection);

        ArticleSearchCriteria searchCriteria = new ArticleSearchCriteria();
        searchCriteria.setTitle(title);
        searchCriteria.setAuthorId(authorId);
        searchCriteria.setTags(tags);

        return articleCriteriaRepository.findAllWithFilters(articlePage, searchCriteria);

    }

    @Override
    public List<Article> getMyArticles(String email_user) {
        Integer authorId = userRepository.findByEmail(email_user).getId();
        return articleRepository.findAllByUserId(authorId);
    }

    @Override
    public void deleteArticle(Integer id_article, String email_user) {
        Article article = articleRepository.findById(id_article).orElseThrow(() -> new ArticleException(String.format("Article with id: %s not found", id_article)));

        if (!article.getUser().getId().equals(userRepository.findByEmail(email_user).getId())) {
            log.error("IN editArticle - Only the creator of the post can edit the article");
            throw new ArticleException("Only the creator of the post can edit the article");
        }
        articleRepository.deleteById(id_article);
    }

    private ArticleStatus getArticleStatus(ArticleRequest request) {
        return request.getStatus().equalsIgnoreCase("PUBLIC") ? ArticleStatus.PUBLIC : ArticleStatus.PRIVATE;
    }

    private void fillArticle(ArticleRequest request, String email, Article article) {
        article.setTitle(request.getTitle());
        article.setText(request.getText());
        article.setStatus(getArticleStatus(request));
        article.setUser(userService.findByEmail(email));
        article.setUpdateAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private void fillTagsArticle(ArticleRequest request, Article article) {
        List<Tag> tags = request.getTags();
        for (Tag tag : tags) {
            Tag newTag = tagRepository.findByName(tag.getName());
            if (newTag == null) {
                tagRepository.save(tag);
                article.getTags().add(tag);
            } else {
                article.getTags().add(newTag);
            }
        }
    }
}
