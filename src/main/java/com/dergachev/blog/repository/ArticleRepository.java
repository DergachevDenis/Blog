package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticleStatus;
import com.dergachev.blog.entity.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByTitleAndUserId(String title, int author_id, Pageable page);
    List<Article> findAllByTitle(String title, Pageable page);
    List<Article> findAllByUserId(int author_id, Pageable page);
    List<Article> findAllByStatus(ArticleStatus status);
    List<Article> findAllByUserId(Integer userId);
}
