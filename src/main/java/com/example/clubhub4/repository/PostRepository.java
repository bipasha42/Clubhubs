package com.example.clubhub4.repository;
import com.example.clubhub4.dto.FeedCardView;
import com.example.clubhub4.entity.Post;
import com.example.clubhub4.dto.PostDTO;
import com.example.clubhub4.dto.PostCardView;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
        select new com.example.clubhub4.dto.PostDTO(
            p.id,
            p.content,
            p.createdAt,
            c.id,
            c.name,
            a.id,
            concat(a.firstName, ' ', a.lastName)
        )
        from Post p
        join p.club c
        join p.author a
        where exists (
            select 1 from Follow f
            where f.club = c and f.user.id = :userId
        )
        or exists (
            select 1 from ClubMember m
            where m.club = c and m.user.id = :userId
        )
        order by p.createdAt desc
        """)
    Page<PostDTO> findFeedForUser(@Param("userId") UUID userId, Pageable pageable);



    @Query("""
        select new com.example.clubhub4.dto.PostCardView(
          p.id,
          p.content,
          p.createdAt,
          (select count(l.id) from PostLike l where l.post = p),
          (select count(c.id) from PostComment c where c.post = p)
        )
        from Post p
        where p.club.id = :clubId
        order by p.createdAt desc
        """)
    Page<PostCardView> findPostCardsByClub(@Param("clubId") UUID clubId, Pageable pageable);

    @Query("select p from Post p join fetch p.club c join fetch p.author a where p.id = :id")
    Optional<Post> findByIdWithRelations(@Param("id") UUID id);

    Optional<Post> findByIdAndClub_Id(UUID id, UUID clubId);

    Optional<Post> findByIdAndAuthor_Id(UUID postId, UUID authorId);

    Optional<Post> findByIdAndAuthor_IdAndClub_Id(UUID id, UUID authorId, UUID clubId);

    boolean existsByIdAndAuthor_Id(UUID id, UUID authorId);

    @Query("""
    select new com.example.clubhub4.dto.FeedCardView(
      p.id,
      c.id,
      c.name,
      concat(a.firstName, ' ', a.lastName),
      p.content,
      p.createdAt,
      (select count(l.id) from PostLike l where l.post = p),
      (select count(pc.id) from PostComment pc where pc.post = p)
    )
    from Post p
    join p.club c
    join p.author a
    where exists (
        select 1 from Follow f
        where f.club = c and f.user.id = :userId
    )
    or exists (
        select 1 from ClubMember m
        where m.club = c and m.user.id = :userId
    )
    order by p.createdAt desc
    """)
    Page<FeedCardView> findFeedCardsForUser(@Param("userId") java.util.UUID userId, Pageable pageable);

}