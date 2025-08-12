package com.example.clubhub4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "university")
@Getter @Setter
public class University {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "country_id", nullable = false)
    private Integer countryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private Country country;
}