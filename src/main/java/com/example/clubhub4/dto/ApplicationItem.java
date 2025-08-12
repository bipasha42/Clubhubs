package com.example.clubhub4.dto;

import com.example.clubhub4.entity.ApplicationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationItem(
        UUID applicationId,
        UUID userId,
        String fullName,
        String email,
        ApplicationStatus status,
        String applicationText,
        OffsetDateTime appliedAt,
        OffsetDateTime processedAt
) {}