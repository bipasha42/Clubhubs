package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "club",
        uniqueConstraints = {
                @UniqueConstraint(name = "club_unique_name_university", columnNames = {"name", "university_id"})
        }
)
@Getter @Setter
public class Club {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    // Keep as UUID to avoid needing a University entity
    @Column(name = "university_id", nullable = false, columnDefinition = "uuid")
    private UUID universityId;

    // FK to users(id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", insertable = false, updatable = false)
    private University university;
}