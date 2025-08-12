package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clubapplication",
        uniqueConstraints = @UniqueConstraint(name = "unique_application", columnNames = {"club_id","user_id"}))
@Getter @Setter
public class ClubApplication {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "application_text", columnDefinition = "text")
    private String applicationText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "applied_at")
    private OffsetDateTime appliedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy; // nullable

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) appliedAt = OffsetDateTime.now();
    }
}