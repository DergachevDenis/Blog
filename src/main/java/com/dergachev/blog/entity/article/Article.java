package com.dergachev.blog.entity.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "article_table")
@Data
@ToString(of = {"title", "text", "created_at", "update_at"})
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

    @Column(name = "author_id", nullable = false)
    private Integer author_id;

    @Column(name = "created_at", nullable = false)
    private Date created_at;

    @Column(name = "update_at", nullable = false)
    private Date update_at;

}
