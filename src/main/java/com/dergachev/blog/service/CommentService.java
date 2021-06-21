package com.dergachev.blog.service;

import com.dergachev.blog.dto.CommentRequest;
import com.dergachev.blog.entity.comment.Comment;

import java.util.List;

public interface CommentService {

    void addComment(CommentRequest request, Integer articleId, String email);

    List<Comment> getComments(Integer articleID, Integer skip, Integer limit, Integer userId, String sort, String order);

    Comment getComment(Integer commentId);

    void deleteComment(Integer id_article, Integer commentId, String email_user);

}
