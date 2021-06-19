package com.dergachev.blog.entity.comment;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "commnet_table")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"text", "created_at"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;


    @Column(name = "article", nullable = false)
    private Integer article_id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer user_id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate created_at;

}
