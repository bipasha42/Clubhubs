package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClubCardView(
        UUID id,
        String name,
        String universityName,
        long followerCount,
        long memberCount

) {}