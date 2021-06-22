package com.dergachev.blog.service;

import com.dergachev.blog.config.TestConfig;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> template;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "dergache.di@gmail.com";
    private static final String TEST_CODE = "TEST CODE";
    private static final String TEST_PASSWORD = "TEST PASSWORD";
    private static final ResetPasswordRequest request = mock(ResetPasswordRequest.class);

    @Test
    public void findByEmailUserIfUserNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(null);

        User user = userService.findByEmail(TEST_EMAIL);
        assertEquals(null, user);
    }

    @Test
    public void findByEmailUserIfUserFound() {
        User userTest = new User();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(userTest);

        User user = userService.findByEmail(TEST_EMAIL);
        assertEquals(userTest, user);
    }
}
