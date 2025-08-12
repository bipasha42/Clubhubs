package com.example.clubhub4.dto;

import java.util.UUID;

public interface ClubCardProjection {
    UUID getId();
    String getName();
    String getUniversityName();
    Long getFollowerCount();
    Long getMemberCount();
}