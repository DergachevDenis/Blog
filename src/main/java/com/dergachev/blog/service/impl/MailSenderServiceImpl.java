package com.dergachev.blog.service.impl;

import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:blog.properties")
public class MailSenderServiceImpl implements MailSenderService {
    private final static String ACTIVATION_MESSAGE = "Hello %s! Welcome to site. Please, visit next link to confirm email: http://localhost:8075/activate/%s";
    private final static String SUBJECT_ACTIVATION = "Activation code";
    private final static String FORGOT_PASSWORD_MESSAGE = "Hello %s! Your password reset code: %s";
    private final static String SUBJECT_FORGOT_PASSWORD = "Forgot Password";

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender emailSender;

    @Autowired
    public MailSenderServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendActivationCode(User user, String activationCode) {
        String message = String.format(ACTIVATION_MESSAGE, user.getFirstName(), activationCode);
        send(user.getEmail(), SUBJECT_ACTIVATION, message);
    }

    @Override
    public void sendForgotPasswordEmail(User user, String code) {
        String message = String.format(FORGOT_PASSWORD_MESSAGE, user.getFirstName(), code);
        send(user.getEmail(), SUBJECT_FORGOT_PASSWORD, message);
    }

    private void send(String emailTo, String subject, String message) throws MailException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        emailSender.send(mailMessage);
    }
}
