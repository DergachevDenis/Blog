package com.dergachev.blog.controller;


import com.dergachev.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping(value = "tags-cloud")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Integer>> getTagsCloud() {
        Map<String, Integer> cloudTags = tagService.getTagsCloud();
        return new ResponseEntity<>(cloudTags, HttpStatus.OK);
    }
}
