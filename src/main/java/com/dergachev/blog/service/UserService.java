package com.dergachev.blog.service;

import com.dergachev.blog.dto.AuthRequest;
import com.dergachev.blog.dto.ForgotPasswordRequest;
import com.dergachev.blog.dto.RegistrationRequest;
import com.dergachev.blog.dto.ResetPasswordRequest;
import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.exception.UserException;

public interface UserService {

    void register(RegistrationRequest user) throws UserException;

    String auth(AuthRequest request) throws UserException;

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findById(Integer id) throws UserException;

    void activateUser(String code) throws UserException;

    void forgotPasswordEmail(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
