package com.dergachev.blog.repository;

import com.dergachev.blog.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "SELECT * FROM comment_table WHERE article_id = :articleId AND user_id = :authorId ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Comment> findAllWithFiltersByAuthorId(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order, @Param("authorId") Integer authorId, @Param("articleId") Integer articleId);

    @Query(value = "SELECT * FROM comment_table WHERE article_id = :articleId ORDER BY :sort :order LIMIT :skip,:limit",
            nativeQuery = true)
    List<Comment> findAllWithFilters(@Param("limit") Integer limit, @Param("skip") Integer skip, @Param("sort") String sort, @Param("order") String order, @Param("articleId") Integer articleId);
}
