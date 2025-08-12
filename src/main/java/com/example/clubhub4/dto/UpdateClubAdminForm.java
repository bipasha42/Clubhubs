package com.example.clubhub4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClubAdminForm {

    @NotBlank @Email
    private String adminEmail;

    @NotBlank @Size(min = 2, max = 100)
    private String adminFirstName;

    @NotBlank @Size(min = 2, max = 100)
    private String adminLastName;

    // Optional: if blank, password remains unchanged
    @Size(min = 6, max = 255)
    private String adminPassword;

    private String adminConfirmPassword;
}