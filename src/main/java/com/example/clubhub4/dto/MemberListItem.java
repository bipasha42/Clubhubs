package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemberListItem(
        UUID userId,
        String fullName,
        String email,
        OffsetDateTime memberSince
) {}