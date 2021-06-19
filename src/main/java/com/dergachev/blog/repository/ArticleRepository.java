package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    //Optional<Article> findById(Integer id)findByFirstNameStartsWith;

    //List<Article> findByTitleAndId_author(String title, int author_id, Pageable page);
    //List<Article> findByTitle(String title, Pageable page);
    List<Article> findAllByStatus(ArticleStatus status);
    List<Article> findAllByAuthorId(Integer id);
}
