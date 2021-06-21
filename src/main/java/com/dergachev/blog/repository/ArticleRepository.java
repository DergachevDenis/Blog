package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    List<Article> findAllByUserId(Integer userId);

    @Query(value = "SELECT * FROM article_table ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Article> findAllWithFilters(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order);

    @Query(value = "SELECT * FROM article_table WHERE title = :title ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Article> findAllWithFiltersByTitle(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order, @Param("title") String title);

    @Query(value = "SELECT * FROM article_table WHERE user_id = :authorId ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Article> findAllWithFiltersByAuthorId(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order, @Param("authorId") Integer authorId);

    @Query(value = "SELECT * FROM article_table WHERE title = :title AND user_id = :authorId ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Article> findAllWithFiltersByTitleAndAuthorId(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order, @Param("title") String title, @Param("authorId") Integer authorId);

    @Query(value = "SELECT u FROM Article u JOIN u.tags t WHERE t.name IN :names")
    Set<Article> findArticleByTagList(@Param("names") List<String> tags);
}
