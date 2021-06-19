package com.dergachev.blog.controller;

import com.dergachev.blog.dto.ArticleRequest;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.service.impl.ArticleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Map<String, Object>> addArticle(ServletRequest servletRequest, @Valid @RequestBody ArticleRequest request, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (getMapResponseError(bindingResult, response)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String email = getEmailFromRequest((HttpServletRequest) servletRequest);
        articleService.addArticle(request, email);
        response.put("Email", email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    private boolean getMapResponseError(BindingResult bindingResult, Map<String, Object> response) {
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
        System.out.println(bearer);
        if (hasText(bearer) && bearer.startsWith(BEARER)) {
            return  jwtProvider.getEmailFromToken(bearer.substring(7));
        }
        return null;
    }
}
