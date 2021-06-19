package com.dergachev.blog.controller;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.service.impl.ArticleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RestController
@RequestMapping("articles")
public class ArticlesController {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Autowired
    ArticleServiceImpl articleService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<Map<String, String>> addArticle(HttpServletRequest  httpServletRequest, @Valid @RequestBody ArticleRequest request, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String email_user = getEmailFromRequest(httpServletRequest);
        try {
            articleService.addArticle(request, email_user);
            response.put("Message", String.format("Article with name %s created!", request.getTitle()));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException exception) {
            log.error("IN addArticle - {}", exception.getMessage());
            response.put("error", exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editArticle(HttpServletRequest  httpServletRequest, @Valid @RequestBody ArticleRequest request, BindingResult bindingResult, @PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String email_user = getEmailFromRequest(httpServletRequest);
        try {
            articleService.editArticle(request, id, email_user);
            response.put("Message", String.format("Article with name %s update!", request.getTitle()));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ArticleException articleException) {
            response.put("error", articleException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataAccessException exception) {
            log.error("IN editArticle - {}", exception.getMessage());
            response.put("error", exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Article>> getPublicArticle(/*@RequestParam Integer skip,
                                                          @RequestParam Integer limit,
                                                          @RequestParam String post_title,
                                                          @RequestParam Integer author,
                                                          @RequestParam String sort*/) {
           List<Article> articles = articleService.getPublicArticles();
           if (articles == null) {
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Article>> getMyArticle(HttpServletRequest  httpServletRequest){
        String email_user = getEmailFromRequest(httpServletRequest);
        List<Article> articles = articleService.getMyArticles(email_user);
        if(articles == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteArticle(HttpServletRequest httpServletRequest, @PathVariable Integer id){
        Map<String, String> response = new HashMap<>();

        String email_user = getEmailFromRequest(httpServletRequest);

        try {
            articleService.deleteArticle(id,email_user);
            response.put("message","Article deleted");
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (ArticleException articleException) {
            response.put("error", articleException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataAccessException exception) {
            log.error("IN editArticle - {}", exception.getMessage());
            response.put("error", exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
