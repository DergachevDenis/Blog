package com.dergachev.blog.junittest.service;

import com.dergachev.blog.junittest.config.TestConfig;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


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
