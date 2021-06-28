package com.dergachev.blog.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class ForgotPasswordRequest {

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email;
}
