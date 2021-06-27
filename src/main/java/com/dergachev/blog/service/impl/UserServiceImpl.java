package com.dergachev.blog.service.impl;

import com.dergachev.blog.dto.AuthRequest;
import com.dergachev.blog.dto.ForgotPasswordRequest;
import com.dergachev.blog.dto.RegistrationRequest;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.entity.user.UserStatus;
import com.dergachev.blog.entity.user.RoleEntity;
import com.dergachev.blog.exception.UserException;
import com.dergachev.blog.jwt.JwtProvider;
import com.dergachev.blog.repository.UserRepository;
import com.dergachev.blog.repository.RoleEntityRepository;
import com.dergachev.blog.service.MailSenderService;
import com.dergachev.blog.service.UserService;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final RoleEntityRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> template;
    private final MailSenderService mailSenderService;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(RoleEntityRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, @Qualifier("redisTemplate") RedisTemplate<String, String> template, MailSenderService mailSenderService, JwtProvider jwtProvider) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.template = template;
        this.mailSenderService = mailSenderService;
        this.jwtProvider = jwtProvider;
    }


    @Transactional(rollbackFor = {UserException.class})
    @Override
    public void register(RegistrationRequest registrationRequest) throws UserException {
        User user = findByEmail(registrationRequest.getEmail());
        if (user != null) {
            log.error("IN register -  User with this email {} already exists", registrationRequest.getEmail());
            throw new UserException("User with this email already exists");
        }

        user = new User();
        createUser(registrationRequest, user);
        userRepository.save(user);

        String activationCode = createHashCode(user.getEmail());

        if (!StringUtils.isNullOrEmpty(user.getEmail())) {
            mailSenderService.sendActivationCode(user, activationCode);
        }
    }

    @Transactional
    @Override
    public String auth(AuthRequest request) throws UserException {
        User user = findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user.getStatus() == UserStatus.NOT_ACTIVE) {
            log.error("IN auth - User with email: {} not activated", request.getEmail());
            throw new UserException(String.format("User with email: %s not activated", request.getEmail()));
        }
        return jwtProvider.generateToken(user.getEmail());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        User result = findByEmail(email);
        if (result == null) {
            throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
        }
        if (!passwordEncoder.matches(password, result.getPassword())) {
            log.error("IN findByEmailAndPassword - user by email: {} invalid password", email);
            throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
        }
        return result;
    }

    @Override
    public User findById(Integer id) throws UserException {
        return userRepository.findById(id).orElseThrow((() -> new UserException(String.format("User with id: %s not found", id))));
    }

    @Transactional
    @Override
    public void activateUser(String code) throws UserException {
        String email = template.opsForValue().get(code);
        if (email == null) {
            log.error("IN activateUser - Activation code expired or not found");
            throw new UserException("Activation code expired or not found");
        }
        User user = userRepository.findByEmail(email);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void forgotPasswordEmail(ForgotPasswordRequest request) throws UserException {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("IN forgotPasswordEmail - user with email: {} not found", email);
            throw new UserException(String.format("User with email: %s not found", request.getEmail()));
        }
        String code = createHashCode(email);
        mailSenderService.sendForgotPasswordEmail(user, code);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) throws UserException {
        String code = request.getCode();
        String email = template.opsForValue().get(code);
        if (email == null) {
            log.error("IN resetPassword - Reset code expired or not found");
            throw new UserException("Reset code expired");
        }
        User user = userRepository.findByEmail(email);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void createUser(RegistrationRequest registrationRequest, User user) {
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getFirst_name());
        user.setLastName(registrationRequest.getLast_name());
        user.setStatus(UserStatus.NOT_ACTIVE);

        RoleEntity authorRole = roleRepository.findByName("ROLE_USER");
        user.setRoleEntity(authorRole);
    }

    private String createHashCode(String email) {
        String code = UUID.randomUUID().toString();
        template.opsForValue().set(code, email);
        template.expire(code, 24, TimeUnit.HOURS);
        return code;
    }
}
