package com.example.clubhub4.dto;

import java.util.UUID;

public record ClubListItem(
        UUID id,
        String name,
        String adminEmail,
        String adminFullName
) {}