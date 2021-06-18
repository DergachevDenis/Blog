package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
}
