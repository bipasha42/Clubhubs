package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostDetailsView(
        UUID postId,
        UUID clubId,
        String clubName,
        String content,
        String imageUrl,          // <-- add
        OffsetDateTime createdAt,
        long likeCount,
        long commentCount,
        boolean likedByCurrentUser
) {}