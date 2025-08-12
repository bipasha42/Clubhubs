package com.example.clubhub4.repository;

import com.example.clubhub4.entity.PostComment;
import com.example.clubhub4.dto.PostCommentView;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    long countByPost_Id(UUID postId);

    @Query("""
        select new com.example.clubhub4.dto.PostCommentView(
          c.id,
          concat(a.firstName, ' ', a.lastName),
          c.content,
          c.createdAt
        )
        from PostComment c
        join c.author a
        where c.post.id = :postId
        order by c.createdAt asc
        """)
    List<PostCommentView> findViewByPostId(@Param("postId") UUID postId);
    Optional<PostComment> findByIdAndPost_Club_Admin_Id(UUID id, UUID adminUserId);
    Optional<PostComment> findByIdAndPost_Author_Id(UUID id, UUID authorUserId);
}