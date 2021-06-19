package com.dergachev.blog.entity.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "article_table")
@Data
@EqualsAndHashCode(of = {"id"})
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column(name = "authorId", nullable = false)
    private Integer authorId;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String createdAt;

    @Column(name = "updateAt", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String updateAt;

}
