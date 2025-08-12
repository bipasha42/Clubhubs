package com.example.clubhub4.service;

import com.example.clubhub4.entity.Post;
import com.example.clubhub4.entity.PostComment;
import com.example.clubhub4.repository.PostCommentRepository;
import com.example.clubhub4.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorPostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public Post requireAuthorOwnedPost(UUID userId, UUID clubId, UUID postId) {
        return postRepository.findByIdAndAuthor_IdAndClub_Id(postId, userId, clubId)
                .orElseThrow(() -> new AccessDeniedException("You can only edit/delete your own post"));
    }

    @Transactional
    public void updatePostAsAuthor(UUID userId, UUID clubId, UUID postId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        Post p = requireAuthorOwnedPost(userId, clubId, postId);
        p.setContent(content.trim());
        p.setUpdatedAt(OffsetDateTime.now());
        postRepository.save(p);
    }

    @Transactional
    public void deletePostAsAuthor(UUID userId, UUID clubId, UUID postId) {
        Post p = requireAuthorOwnedPost(userId, clubId, postId);
        postRepository.delete(p);
    }

    @Transactional
    public void deleteCommentAsPostAuthor(UUID userId, UUID postId, UUID commentId) {
        PostComment c = postCommentRepository.findByIdAndPost_Author_Id(commentId, userId)
                .orElseThrow(() -> new AccessDeniedException("You can only delete comments on your own post"));

        // Optional safety: ensure comment belongs to the given postId
        if (!c.getPost().getId().equals(postId)) {
            throw new AccessDeniedException("Comment does not belong to this post");
        }
        postCommentRepository.delete(c);
    }
}