package com.dergachev.blog.service;

import com.dergachev.blog.entity.user.User;

public interface MailSenderService {
    void sendActivationCode(User user, String activationCode);
    void sendForgotPasswordEmail(User user, String code);
}
