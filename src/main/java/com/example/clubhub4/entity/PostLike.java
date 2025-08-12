package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "postlikes",
        uniqueConstraints = @UniqueConstraint(name = "unique_post_like", columnNames = {"post_id", "user_id"}))
@Getter @Setter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // serial

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}