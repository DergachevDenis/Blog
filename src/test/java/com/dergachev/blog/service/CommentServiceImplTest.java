package com.dergachev.blog.service;

import com.dergachev.blog.config.TestConfig;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.CommentException;
import com.dergachev.blog.exception.NotFoundException;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.repository.ArticleRepository;
import com.dergachev.blog.repository.CommentRepository;
import com.dergachev.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class CommentServiceImplTest {

    @Autowired
    CommentService commentService;

    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;

    private static final String TEST_EMAIL = "dergache.di@gmail.com";
    private static final Integer TEST_ID = 2;
}