package com.example.clubhub4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostForm {
    @NotBlank
    @Size(min = 1, max = 10000)
    private String content;
}