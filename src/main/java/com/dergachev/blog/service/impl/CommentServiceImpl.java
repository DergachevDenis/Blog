package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.CommentRequest;
import com.dergachev.blog.entity.comment.Comment;
import com.dergachev.blog.exception.CommentException;
import com.dergachev.blog.repository.CommentRepository;
import com.dergachev.blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CommentRepository commentRepository;


    @Override
    public void addComment(CommentRequest request, Integer articleId, String email) {
        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setArticleId(articleId);
        comment.setUserId(userService.findByEmail(email).getId());
        comment.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> getComments(Integer articleID, Integer skip, Integer limit, Integer userId, String sort) {
        if (userId == null) {
            return commentRepository.findAllByArticleId(articleID, PageRequest.of(skip, skip + limit, Sort.by(sort)));
        }
        return commentRepository.findByArticleIdAndUserId(articleID, userId, PageRequest.of(skip, skip + limit, Sort.by(sort)));
    }

    @Override
    public Comment getComment(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(()->new CommentException(String.format("Comment with id %s not found", commentId)));
    }

    @Override
    public void deleteComment(Integer id_article, Integer commentId) {

    }
}
