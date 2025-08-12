package com.example.clubhub4.service;

import com.example.clubhub4.entity.*;
import com.example.clubhub4.dto.*;
import com.example.clubhub4.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubAdminService {

    private final ClubRepository clubRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final PostCommentRepository postCommentRepository;

    // Fetch the club owned by this admin
    public Club getMyClub(UUID adminUserId) {
        return clubRepository.findByAdmin_Id(adminUserId)
                .orElseThrow(() -> new IllegalStateException("No club is assigned to your admin account."));
    }

    public ClubCardView getMyClubCard(UUID adminUserId) {
        Club club = getMyClub(adminUserId);
        return clubRepository.findClubCard(club.getId())
                .orElseThrow(() -> new IllegalStateException("Club not found."));
    }

    // 1) Admin feed: only posts from their club
    public Page<PostCardView> listMyFeed(UUID adminUserId, int page, int size) {
        Club club = getMyClub(adminUserId);
        return postRepository.findPostCardsByClub(club.getId(), PageRequest.of(page, size));
    }

    // 2) CRUD posts — enforced to club ownership
    @Transactional
    public UUID createPost(UUID adminUserId, PostForm form) {
        Club club = getMyClub(adminUserId);
        User author = userRepository.findById(adminUserId).orElseThrow();

        Post p = new Post();
        p.setClub(club);
        p.setAuthor(author);
        p.setContent(form.getContent().trim());
        p.setCreatedAt(OffsetDateTime.now());
        p.setUpdatedAt(OffsetDateTime.now());

        p = postRepository.save(p);
        return p.getId();
    }

    public Post getMyClubPost(UUID adminUserId, UUID postId) {
        Club club = getMyClub(adminUserId);
        return postRepository.findByIdAndClub_Id(postId, club.getId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found or not in your club."));
    }

    @Transactional
    public void updatePost(UUID adminUserId, UUID postId, PostForm form) {
        Post p = getMyClubPost(adminUserId, postId);
        p.setContent(form.getContent().trim());
        p.setUpdatedAt(OffsetDateTime.now());
        postRepository.save(p);
    }

    @Transactional
    public void deletePost(UUID adminUserId, UUID postId) {
        Post p = getMyClubPost(adminUserId, postId);
        postRepository.delete(p);
    }

    // 4) Members list with remove operation
    public Page<MemberListItem> listMembers(UUID adminUserId, int page, int size) {
        Club club = getMyClub(adminUserId);
        return clubMemberRepository.findMembersByClubId(club.getId(), PageRequest.of(page, size, Sort.by("memberSince").descending()));
    }

    @Transactional
    public void removeMember(UUID adminUserId, UUID memberUserId) {
        Club club = getMyClub(adminUserId);
        // Don’t allow removing the club admin (safety)
        if (club.getAdmin() != null && club.getAdmin().getId().equals(memberUserId)) {
            throw new IllegalArgumentException("You cannot remove the club admin.");
        }
        long deleted = clubMemberRepository.deleteByClub_IdAndUser_Id(club.getId(), memberUserId);
        if (deleted == 0) {
            throw new IllegalArgumentException("Membership not found.");
        }
    }

    public record DeleteCommentResult(UUID postId, UUID clubId) {}

    @Transactional
    public DeleteCommentResult deleteComment(UUID adminUserId, UUID commentId) {
        PostComment c = postCommentRepository
                .findByIdAndPost_Club_Admin_Id(commentId, adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found or not in your club."));
        UUID postId = c.getPost().getId();
        UUID clubId = c.getPost().getClub().getId();
        postCommentRepository.delete(c);
        return new DeleteCommentResult(postId, clubId);
    }
}