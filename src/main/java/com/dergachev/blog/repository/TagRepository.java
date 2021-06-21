package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Tag findByName(String name);
}
