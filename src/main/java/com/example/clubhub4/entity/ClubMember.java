package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clubmember",
        uniqueConstraints = @UniqueConstraint(name = "unique_club_member", columnNames = {"club_id", "user_id"}))
@Getter @Setter
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // serial

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "member_since")
    private OffsetDateTime memberSince;
}