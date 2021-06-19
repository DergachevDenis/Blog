package com.dergachev.blog.service;

import com.dergachev.blog.dto.CommentRequest;
import com.dergachev.blog.entity.comment.Comment;

import java.util.List;

public interface CommentService {

    void addComment(CommentRequest request, Integer articleId, String email);
    List<Comment> getComments(Integer articleID, Integer skip, Integer limit, Integer authorId, String sort);
    List<Comment> getComment(Integer articleId, Integer commentId);
    void deleteArticle(Integer id_article, Integer commentId);

}
