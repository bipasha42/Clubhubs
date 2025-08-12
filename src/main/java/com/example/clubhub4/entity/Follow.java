package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "follow",
        uniqueConstraints = @UniqueConstraint(name = "unique_follow", columnNames = {"club_id", "user_id"}))
@Getter @Setter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // serial

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}