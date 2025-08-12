package com.example.clubhub4.repository;

import com.example.clubhub4.dto.MyClubOption;
import com.example.clubhub4.entity.ClubMember;
import com.example.clubhub4.dto.MemberListItem;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    @Query(value = """
        select new com.example.clubhub4.dto.MemberListItem(
            u.id, concat(u.firstName, ' ', u.lastName), u.email, m.memberSince
        )
        from ClubMember m
        join m.user u
        where m.club.id = :clubId
        order by m.memberSince desc
        """,
            countQuery = """
        select count(m)
        from ClubMember m
        where m.club.id = :clubId
        """)
    Page<MemberListItem> findMembersByClubId(@Param("clubId") UUID clubId, Pageable pageable);

    boolean existsByClub_IdAndUser_Id(UUID clubId, UUID userId);

    @Query("""
        select new com.example.clubhub4.dto.MyClubOption(
            c.id, c.name, u.name
        )
        from ClubMember m
        join m.club c
        join c.university u
        where m.user.id = :userId
        order by c.name asc
        """)
    List<MyClubOption> findClubOptionsByUserId(@Param("userId") UUID userId);

    Optional<ClubMember> findByClub_IdAndUser_Id(UUID clubId, UUID userId);

    long deleteByClub_IdAndUser_Id(UUID clubId, UUID userId);
}