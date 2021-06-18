package com.dergachev.blog.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "(^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$)", message = "Password must contain 6 characters using numbers, symbols, Latin alphabet, lowercase and uppercase characters")
    private String newPassword;
}
