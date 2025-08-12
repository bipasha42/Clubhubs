package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostDTO(
        UUID postId,
        String content,
        OffsetDateTime createdAt,
        UUID clubId,
        String clubName,
        UUID authorId,
        String authorFullName
) {}