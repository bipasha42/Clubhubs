package com.example.clubhub4.repository;

import com.example.clubhub4.entity.ApplicationStatus;
import com.example.clubhub4.entity.ClubApplication;
import com.example.clubhub4.dto.ApplicationItem;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ClubApplicationRepository extends JpaRepository<ClubApplication, UUID> {

    boolean existsByClub_IdAndUser_Id(UUID clubId, UUID userId);
    Optional<ClubApplication> findByClub_IdAndUser_Id(UUID clubId, UUID userId);

    @Query("""
        select new com.example.clubhub4.dto.ApplicationItem(
          a.id, u.id, concat(u.firstName,' ',u.lastName), u.email,
          a.status, a.applicationText, a.appliedAt, a.processedAt
        )
        from ClubApplication a
        join a.user u
        where a.club.id = :clubId
        order by a.appliedAt desc
        """)
    Page<ApplicationItem> listForClub(@Param("clubId") UUID clubId, Pageable pageable);

    @Query("""
        select a from ClubApplication a
        join a.club c
        where a.id = :appId and c.admin.id = :adminUserId
        """)
    Optional<ClubApplication> findByIdForAdmin(@Param("appId") UUID appId, @Param("adminUserId") UUID adminUserId);

    // For explore page status badges
    @Query("select a from ClubApplication a where a.user.id = :userId and a.club.id in :clubIds")
    List<ClubApplication> findByUserAndClubs(@Param("userId") UUID userId, @Param("clubIds") Collection<UUID> clubIds);
}