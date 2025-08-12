package com.example.clubhub4.dto;

import com.example.clubhub4.entity.ApplicationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationView(
        UUID id,
        String message,
        boolean read,
        OffsetDateTime createdAt,
        UUID applicationId,
        UUID clubId,
        String clubName,
        ApplicationStatus status
) {}