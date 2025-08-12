package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostCommentView(
        UUID id,
        String authorFullName,
        String content,
        OffsetDateTime createdAt
) {}