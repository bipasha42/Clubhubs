package com.example.clubhub4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpForm {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank @Size(min = 2, max = 100)
    private String lastName;

    // you asked for no encoding; these will be stored as plain text (not safe for prod)
    @NotBlank @Size(min = 6, max = 255)
    private String password;

    @NotBlank
    private String confirmPassword;
}