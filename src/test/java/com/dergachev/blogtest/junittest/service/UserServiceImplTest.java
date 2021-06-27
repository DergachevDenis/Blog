package com.dergachev.blogtest.junittest.service;

import com.dergachev.blog.dto.AuthRequest;
import com.dergachev.blog.dto.ForgotPasswordRequest;
import com.dergachev.blog.dto.RegistrationRequest;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.UserStatus;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.service.MailSenderService;
import com.dergachev.blog.service.impl.UserServiceImpl;
import com.dergachev.blogtest.junittest.config.TestConfig;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> template;
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private JwtProvider jwtProvider;

    private static final String TEST_EMAIL = "dergache.di@gmail.com";
    private static final String TEST_PASSWORD = "Denis123";
    private static final String TEST_FIRST_NAME = "Denis";
    private static final String TEST_LAST_NAME = "Dergachev";
    private static final String TEST_TOKEN = "TOKEN";
    private static final String TEST_CODE = "Code";

    @BeforeEach
    public void initTest(){
        reset(userRepository);
        reset(mailSenderService);
    }

    @Test
    public void findByEmailTestIfUserNotFound() {
        doReturn(null).when(userRepository).findByEmail(TEST_EMAIL);

        User user = userService.findByEmail(TEST_EMAIL);

        assertNull(user);
    }

    @Test
    public void findByEmailTest() {
        User userTest = new User();
        doReturn(userTest).when(userRepository).findByEmail(TEST_EMAIL);

        User user = userService.findByEmail(TEST_EMAIL);

        assertEquals(userTest, user);
    }

    @Test
    public void registerTest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirst_name(TEST_FIRST_NAME);
        request.setLast_name(TEST_LAST_NAME);

        ValueOperations mock = mock(ValueOperations.class);
        doReturn(mock).when(template).opsForValue();
        doNothing().when(mock).set(TEST_EMAIL, TEST_CODE);

        userService.register(request);

        verify(userRepository, Mockito.times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, Mockito.times(1)).save(any());
        verify(mailSenderService, Mockito.times(1)).sendActivationCode(any(), anyString());
    }

    @Test
    public void registerTestExistsEmail() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirst_name(TEST_FIRST_NAME);
        request.setLast_name(TEST_LAST_NAME);

        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(new User()).when(userServiceSpy).findByEmail(request.getEmail());

        assertThrows(
                UserException.class,
                () -> userServiceSpy.register(request));
    }

    @Test
    public void authTest() {
        User testUser = new User();
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setEmail(TEST_EMAIL);

        AuthRequest request = new AuthRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(testUser).when(userServiceSpy).findByEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);
        doReturn(TEST_TOKEN).when(jwtProvider).generateToken(testUser.getEmail());

        String testToken = userServiceSpy.auth(request);

        assertEquals(testToken, TEST_TOKEN);
    }

    @Test
    public void authTestIfUserNotActive() {
        User testUser = new User();
        testUser.setStatus(UserStatus.NOT_ACTIVE);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setEmail(TEST_EMAIL);

        AuthRequest request = new AuthRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(testUser).when(userServiceSpy).findByEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

        assertThrows(
                UserException.class,
                () -> userServiceSpy.auth(request));
    }

    @Test
    public void findByEmailAndPasswordTest() {
        User testUser = new User();
        testUser.setPassword(TEST_PASSWORD);
        testUser.setEmail(TEST_EMAIL);

        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(testUser).when(userServiceSpy).findByEmail(TEST_EMAIL);
        doReturn(true).when(passwordEncoder).matches(TEST_PASSWORD, testUser.getPassword());

        User result = userServiceSpy.findByEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

        assertEquals(result, testUser);
    }

    @Test
    public void findByEmailAndPasswordTestIfFindByEmailNull() {
        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(null).when(userServiceSpy).findByEmail(TEST_EMAIL);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userServiceSpy.findByEmailAndPassword(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    public void findByEmailAndPasswordTestIfPasswordsNotMatch() {
        User testUser = new User();
        testUser.setPassword(TEST_PASSWORD);
        testUser.setEmail(TEST_EMAIL);

        UserServiceImpl userServiceSpy = Mockito.spy(userService);
        doReturn(testUser).when(userServiceSpy).findByEmail(TEST_EMAIL);
        doReturn(false).when(passwordEncoder).matches(TEST_PASSWORD, testUser.getPassword());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userServiceSpy.findByEmailAndPassword(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    public void activateUserTest() {
        User testUser = new User();
        ValueOperations mock = mock(ValueOperations.class);
        doReturn(mock).when(template).opsForValue();
        doReturn(TEST_EMAIL).when(mock).get(TEST_CODE);
        doReturn(testUser).when(userRepository).findByEmail(TEST_EMAIL);

        userService.activateUser(TEST_CODE);

        assertEquals(testUser.getStatus(), UserStatus.ACTIVE);
        verify(userRepository, Mockito.times(1)).save(testUser);
    }

    @Test
    public void activateUserTestIfCodeExpiredOrNotFound() {
        ValueOperations mock = mock(ValueOperations.class);
        doReturn(mock).when(template).opsForValue();
        doReturn(null).when(mock).get(TEST_CODE);

        assertThrows(
                UserException.class,
                () -> userService.activateUser(TEST_CODE));
    }

    @Test
    public void forgotPasswordEmailTest() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(TEST_EMAIL);
        User testUser = new User();
        doReturn(testUser).when(userRepository).findByEmail(forgotPasswordRequest.getEmail());

        ValueOperations mock2 = mock(ValueOperations.class);
        doReturn(mock2).when(template).opsForValue();
        doNothing().when(mock2).set(TEST_CODE, TEST_EMAIL);

        userService.forgotPasswordEmail(forgotPasswordRequest);

        verify(mailSenderService, Mockito.times(1)).sendForgotPasswordEmail(any(), any());
    }

    @Test
    public void forgotPasswordEmailTestNotFoundUser() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        doReturn(null).when(userRepository).findByEmail(TEST_EMAIL);

        assertThrows(
                UserException.class,
                () -> userService.forgotPasswordEmail(forgotPasswordRequest));
    }

    @Test
    public void resetPasswordTest() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setCode(TEST_CODE);
        resetPasswordRequest.setNewPassword(TEST_PASSWORD);

        User testUser = new User();

        ValueOperations mock = mock(ValueOperations.class);
        doReturn(mock).when(template).opsForValue();
        doReturn(TEST_EMAIL).when(mock).get(TEST_CODE);
        doReturn(TEST_PASSWORD).when(passwordEncoder).encode(resetPasswordRequest.getNewPassword());
        doReturn(testUser).when(userRepository).findByEmail(TEST_EMAIL);

        userService.resetPassword(resetPasswordRequest);

        assertEquals(testUser.getPassword(), TEST_PASSWORD);
        verify(userRepository, Mockito.times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, Mockito.times(1)).save(testUser);
    }

    @Test
    public void resetPasswordTestIfResetCodeExpiredOrNotFound() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        ValueOperations mock = mock(ValueOperations.class);
        doReturn(mock).when(template).opsForValue();
        doReturn(null).when(mock).get(TEST_CODE);

        assertThrows(
                UserException.class,
                () -> userService.resetPassword(resetPasswordRequest));
    }
}
