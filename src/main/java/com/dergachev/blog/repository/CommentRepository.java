package com.dergachev.blog.repository;

import com.dergachev.blog.entity.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByArticleIdAndUserId(int articleId, int userId, Pageable page);
    List<Comment> findAllByArticleId(int articleId, Pageable page);
}
