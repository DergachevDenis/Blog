package com.dergachev.blog.entity.comment;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "comment_table")
@Data
@EqualsAndHashCode(of = {"id"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "message", nullable = false)
    private String message;


    @Column(name = "articleId", nullable = false)
    private Integer articleId;

    @Column(name = "userId", nullable = false, updatable = false)
    private Integer userId;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private String createdAt;

}
