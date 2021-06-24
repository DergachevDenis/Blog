package com.dergachev.blog.controller;

import com.dergachev.blog.dto.CommentRequest;
import com.dergachev.blog.entity.comment.Comment;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.service.impl.CommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RestController
@RequestMapping(value = "articles/{articleId}/comments")
public class CommentController {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final CommentServiceImpl commentService;
    private final JwtProvider jwtProvider;

    @Autowired
    public CommentController(CommentServiceImpl commentService, JwtProvider jwtProvider) {
        this.commentService = commentService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addComment(HttpServletRequest httpServletRequest,
                                                          @Valid @RequestBody CommentRequest request,
                                                          BindingResult bindingResult, @PathVariable Integer articleId) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String email_user = getEmailFromRequest(httpServletRequest);

        commentService.addComment(request, articleId, email_user);
        response.put("Message", String.format("Comment with text %s added in article!", request.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Integer articleId,
                                                     @RequestParam(name = "skip", required = false, defaultValue = "0") Integer skip,
                                                     @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
                                                     @RequestParam(name = "author", required = false) Integer authorId,
                                                     @RequestParam(name = "sort", required = false, defaultValue = "message") String sort,
                                                     @RequestParam(name = "order", required = false, defaultValue = "ASC") String order) {

        List<Comment> comments = commentService.getComments(articleId, skip, limit, authorId, sort, order);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Integer commentId) {

        Comment comment = commentService.getComment(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(HttpServletRequest httpServletRequest,
                                                             @PathVariable Integer articleId,
                                                             @PathVariable Integer commentId) {
        Map<String, String> response = new HashMap<>();

        String email_user = getEmailFromRequest(httpServletRequest);

        try {
            commentService.deleteComment(articleId, commentId, email_user);
            response.put("message", "Comment deleted");
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (ArticleException articleException) {
            response.put("error", articleException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    private boolean getMapResponseError(BindingResult bindingResult, Map<String, String> response) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                response.put(error.getField(), error.getDefaultMessage());
            }
            return true;
        }
        return false;
    }

    private String getEmailFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith(BEARER)) {
            return jwtProvider.getEmailFromToken(bearer.substring(7));
        }
        return null;
    }
}
