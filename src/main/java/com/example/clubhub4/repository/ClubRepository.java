package com.example.clubhub4.repository;
import com.example.clubhub4.dto.ClubCardView;
import com.example.clubhub4.dto.ClubCardProjection;
import com.example.clubhub4.entity.Club;
import com.example.clubhub4.dto.ClubListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {

    boolean existsByNameIgnoreCaseAndUniversityId(String name, UUID universityId);

    @Query("""
        select new com.example.clubhub4.dto.ClubListItem(
            c.id, c.name, a.email, concat(a.firstName, ' ', a.lastName)
        )
        from Club c
        join c.admin a
        where c.universityId = :universityId
        order by c.name asc
        """)
    Page<ClubListItem> findListByUniversityId(@Param("universityId") UUID universityId, Pageable pageable);

    @Query("select c from Club c join fetch c.admin where c.id = :id")
    Optional<Club> findByIdWithAdmin(@Param("id") UUID id);
    Optional<Club> findByAdmin_Id(UUID adminUserId);


    @Query("""
        select new com.example.clubhub4.dto.ClubCardView(
          c.id,
          c.name,
          (select u.name from University u where u.id = c.universityId),
          (select count(f.id) from Follow f where f.club = c),
          (select count(m.id) from ClubMember m where m.club = c)
        )
        from Club c
        order by c.name asc
        """)
    Page<ClubCardView> findExploreClubs(Pageable pageable);

    @Query("""
        select new com.example.clubhub4.dto.ClubCardView(
          c.id,
          c.name,
          (select u.name from University u where u.id = c.universityId),
          (select count(f.id) from Follow f where f.club = c),
          (select count(m.id) from ClubMember m where m.club = c)
        )
        from Club c
        where c.id = :id
        """)
    Optional<ClubCardView> findClubCard(@Param("id") UUID id);

    @Query(value = """
        select
          c.id as id,
          c.name as name,
          u.name as universityName,
          (select count(*) from follow f where f.club_id = c.id) as followerCount,
          (select count(*) from clubmember m where m.club_id = c.id) as memberCount
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        order by c.name asc
        """,
            countQuery = """
        select count(*)
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        """,
            nativeQuery = true)
    Page<ClubCardProjection> searchByNameNative(@Param("q") String q,
                                                @Param("countryId") Integer countryId,
                                                @Param("universityId") UUID universityId,
                                                Pageable pageable);

    @Query(value = """
        select
          c.id as id,
          c.name as name,
          u.name as universityName,
          (select count(*) from follow f where f.club_id = c.id) as followerCount,
          (select count(*) from clubmember m where m.club_id = c.id) as memberCount
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        order by followerCount desc, c.name asc
        """,
            countQuery = """
        select count(*)
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        """,
            nativeQuery = true)
    Page<ClubCardProjection> searchByFollowersNative(@Param("q") String q,
                                                     @Param("countryId") Integer countryId,
                                                     @Param("universityId") UUID universityId,
                                                     Pageable pageable);

    @Query(value = """
        select
          c.id as id,
          c.name as name,
          u.name as universityName,
          (select count(*) from follow f where f.club_id = c.id) as followerCount,
          (select count(*) from clubmember m where m.club_id = c.id) as memberCount
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        order by memberCount desc, c.name asc
        """,
            countQuery = """
        select count(*)
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        """,
            nativeQuery = true)
    Page<ClubCardProjection> searchByMembersNative(@Param("q") String q,
                                                   @Param("countryId") Integer countryId,
                                                   @Param("universityId") UUID universityId,
                                                   Pageable pageable);

    @Query(value = """
        select
          c.id as id,
          c.name as name,
          u.name as universityName,
          (select count(*) from follow f where f.club_id = c.id) as followerCount,
          (select count(*) from clubmember m where m.club_id = c.id) as memberCount
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        order by (select max(p.created_at) from post p where p.club_id = c.id) desc nulls last,
                 c.name asc
        """,
            countQuery = """
        select count(*)
        from club c
        join university u on u.id = c.university_id
        where (:q is null
               or c.name ilike concat('%', cast(:q as varchar), '%')
               or u.name ilike concat('%', cast(:q as varchar), '%'))
          and (:universityId is null or c.university_id = :universityId)
          and (:countryId is null or u.country_id = :countryId)
        """,
            nativeQuery = true)
    Page<ClubCardProjection> searchByRecentNative(@Param("q") String q,
                                                  @Param("countryId") Integer countryId,
                                                  @Param("universityId") UUID universityId,
                                                  Pageable pageable);
    boolean existsByIdAndAdmin_Id(UUID id, UUID adminUserId);

}