package com.dergachev.blog.service.impl;

import com.dergachev.blog.entity.article.Tag;
import com.dergachev.blog.repository.TagRepository;
import com.dergachev.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Map<String, Integer> getTagsCloud() {
        Map<String, Integer> tagsCloud = new HashMap<>();
        List<Tag> tagList = tagRepository.findAll();
        for (Tag tag : tagList) {
            tagsCloud.put(tag.getName(), tag.getArticles().size());
        }
        return tagsCloud;
    }
}
