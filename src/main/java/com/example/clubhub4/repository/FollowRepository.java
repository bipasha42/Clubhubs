package com.example.clubhub4.repository;

import com.example.clubhub4.entity.Follow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByClub_IdAndUser_Id(UUID clubId, UUID userId);
    Optional<Follow> findByClub_IdAndUser_Id(UUID clubId, UUID userId);
    long countByClub_Id(UUID clubId);

    @Query("select f.club.id from Follow f where f.user.id = :userId and f.club.id in :clubIds")
    Set<UUID> findFollowedClubIds(@Param("userId") UUID userId, @Param("clubIds") Collection<UUID> clubIds);
}