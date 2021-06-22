package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.CommentRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.comment.Comment;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.CommentException;
import com.dergachev.blog.exception.NotFoundException;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.CommentRepository;
import com.dergachev.blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final UserServiceImpl userService;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public CommentServiceImpl(UserServiceImpl userService, CommentRepository commentRepository, ArticleRepository articleRepository) {
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public void addComment(CommentRequest request, Integer articleId, String email) {
        Article article = articleRepository.findById(articleId).orElseThrow(()->new NotFoundException(String.format("Article with %s not found", articleId)));
        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setArticle(article);
        comment.setUser(userService.findByEmail(email));
        comment.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> getComments(Integer articleID, Integer skip, Integer limit, Integer userId, String sort, String order) {
        if (userId == null) {
            return commentRepository.findAllWithFilters(limit, skip, sort, order, articleID);
        }
        return commentRepository.findAllWithFiltersByAuthorId(limit, skip, sort,order, userId, articleID);
    }

    @Override
    public Comment getComment(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("Comment with id %s not found", commentId)));
    }

    @Override
    public void deleteComment(Integer articleId, Integer commentId, String email_user) {
        User user = userService.findByEmail(email_user);
        User authorComment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("Comment with id: %s not found", commentId))).getUser();
        User authorArticle = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException(String.format("Article with id: %s not found", articleId))).getUser();

        if (user.equals(authorArticle) || user.equals(authorComment)) {
            commentRepository.deleteById(commentId);
        } else {
            log.error("IN deleteComment - comment can only be deleted by the author of the article or the author of the comment.");
            throw new CommentException("A comment can only be deleted by the author of the article or the author of the comment!");
        }
    }
}
