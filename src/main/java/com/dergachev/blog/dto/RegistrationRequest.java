package com.dergachev.blog.dto;


import lombok.Data;
import lombok.Value;

import javax.validation.constraints.*;

@Data
public class RegistrationRequest {
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}", message = "Password must contain 6 characters, Contains at least one digit and letter(lowercase and uppercase) characters")
    private String password;

    @NotBlank(message = "FirstName cannot be empty")
    private String first_name;

    @NotBlank(message = "LastName cannot be empty")
    private String last_name;
}
