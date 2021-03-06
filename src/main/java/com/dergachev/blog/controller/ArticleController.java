package com.dergachev.blog.controller;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.exception.ArticleException;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RestController
@RequestMapping(value = "articles")
@Validated
public class ArticleController {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final ArticleService articleService;
    private final JwtProvider jwtProvider;

    @Autowired
    public ArticleController(ArticleService articleService, JwtProvider jwtProvider) {
        this.articleService = articleService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addArticle(HttpServletRequest httpServletRequest,
                                                          @Valid @RequestBody ArticleRequest request,
                                                          BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        System.out.println(request);
        System.out.println(bindingResult);
        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String email_user = getEmailFromRequest(httpServletRequest);
        try {
            articleService.addArticle(request, email_user);
        }
        catch (ArticleException exception){
            response.put("message", exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.put("Message", String.format("Article with name %s created!", request.getTitle()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editArticle(HttpServletRequest httpServletRequest,
                                                           @Valid @RequestBody ArticleRequest request,
                                                           BindingResult bindingResult,
                                                           @PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String email_user = getEmailFromRequest(httpServletRequest);
            articleService.editArticle(request, id, email_user);
            response.put("Message", String.format("Article with name %s update!", request.getTitle()));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ArticleException articleException) {
            response.put("message", articleException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    public ResponseEntity<List<Article>> getArticles(@RequestParam(name = "skip", required = false, defaultValue = "0") Integer skip,
                                                     @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
                                                     @RequestParam(name = "q", required = false) String post_title,
                                                     @RequestParam(name = "author", required = false) Integer authorId,
                                                     @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
                                                     @RequestParam(name = "order", required = false, defaultValue = "ASC") String order) {

        List<Article> articles = articleService.getArticles(skip, limit, post_title, authorId, sort, order);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/tags")
    public ResponseEntity<Set<Article>> getArticlesTags(@RequestParam(name = "tags", required = false) List<String> tags) {

        Set<Article> articles = articleService.getArticlesTags(tags);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Article>> getMyArticles(HttpServletRequest httpServletRequest) {
        String email_user = getEmailFromRequest(httpServletRequest);
        List<Article> articles = articleService.getMyArticles(email_user);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteArticle(HttpServletRequest httpServletRequest, @PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        try {
            String email_user = getEmailFromRequest(httpServletRequest);
            articleService.deleteArticle(id, email_user);
            response.put("message", "Article deleted");
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (ArticleException articleException) {
            response.put("message", articleException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
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
