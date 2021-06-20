package com.dergachev.blog.controller;


import com.dergachev.blog.service.TagService;
import com.dergachev.blog.service.impl.TagServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Map;

@Transactional
@RestController
@RequestMapping("tags-cloud")
public class TagController {

    private final TagServiceImpl tagService;

    @Autowired
    public TagController(TagServiceImpl tagService) {
        this.tagService = tagService;
    }
    @GetMapping
    public ResponseEntity<Map<String, Integer>> getPublicArticles() {
        Map<String, Integer> cloudTags = tagService.getTagsCloud();
        return new ResponseEntity<>(cloudTags, HttpStatus.OK);
    }
}
