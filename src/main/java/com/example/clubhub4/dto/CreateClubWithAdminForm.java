package com.example.clubhub4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClubWithAdminForm {

    // Club fields
    @NotBlank @Size(min = 2, max = 200)
    private String clubName;

    @Size(max = 5000)
    private String description;

    private String logoUrl;
    private String bannerUrl;

    // Club Admin fields (new account)
    @NotBlank @Email
    private String adminEmail;

    @NotBlank @Size(min = 2, max = 100)
    private String adminFirstName;

    @NotBlank @Size(min = 2, max = 100)
    private String adminLastName;

    @NotBlank @Size(min = 6, max = 255)
    private String adminPassword;

    @NotBlank
    private String adminConfirmPassword;
}