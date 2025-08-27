package com.example.clubhub4.service;

import com.example.clubhub4.dto.*;
import com.example.clubhub4.entity.*;
import com.example.clubhub4.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExploreService {

    private final ClubRepository clubRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;


    public Page<ClubCardView> listClubs(int page, int size) {
        return clubRepository.findExploreClubs(PageRequest.of(page, size, Sort.by("name").ascending()));
    }

    public ClubCardView getClubCard(UUID clubId) {
        return clubRepository.findClubCard(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));
    }

    public Page<PostCardView> listClubPosts(UUID clubId, int page, int size) {
        return postRepository.findPostCardsByClub(clubId, PageRequest.of(page, size));
    }

    public PostDetailsView getPostDetails(UUID postId, UUID currentUserIdOrNull) {
        var p = postRepository.findByIdWithRelations(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        long likeCount = likeRepository.countByPost_Id(postId);
        long commentCount = commentRepository.countByPost_Id(postId);
        boolean liked = currentUserIdOrNull != null && likeRepository.existsByPost_IdAndUser_Id(postId, currentUserIdOrNull);

        return new PostDetailsView(
                p.getId(),
                p.getClub().getId(),
                p.getClub().getName(),
                p.getContent(),
                p.getImageUrl(),      // <-- add
                p.getCreatedAt(),
                likeCount,
                commentCount,
                liked
        );
    }

    public boolean isPostAuthor(UUID postId, UUID userId) {
        return userId != null && postRepository.existsByIdAndAuthor_Id(postId, userId);
    }

    public List<PostCommentView> getComments(UUID postId) {
        return commentRepository.findViewByPostId(postId);
    }

    @Transactional
    public long toggleLike(UUID postId, UUID userId) {
        PostLike existing = likeRepository.findByPost_IdAndUser_Id(postId, userId).orElse(null);
        if (existing != null) {
            likeRepository.delete(existing);
        } else {
            PostLike l = new PostLike();
            Post ref = new Post();
            ref.setId(postId);
            l.setPost(ref);
            User u = new User();
            u.setId(userId);
            l.setUser(u);
            likeRepository.save(l);
        }
        return likeRepository.countByPost_Id(postId);
    }

    public boolean isAdminOfClub(UUID clubId, UUID userId) {
        return userId != null && clubRepository.existsByIdAndAdmin_Id(clubId, userId);
    }

    public Set<UUID> followedClubIds(UUID userId, Collection<UUID> clubIds) {
        if (userId == null || clubIds == null || clubIds.isEmpty()) return Set.of();
        return followRepository.findFollowedClubIds(userId, clubIds);
    }

    public boolean isFollowing(UUID clubId, UUID userId) {
        return userId != null && followRepository.existsByClub_IdAndUser_Id(clubId, userId);
    }

    @Transactional
    public long toggleFollow(UUID clubId, UUID userId) {
        var existing = followRepository.findByClub_IdAndUser_Id(clubId, userId).orElse(null);
        if (existing != null) {
            followRepository.delete(existing);
        } else {
            var f = new Follow();
            var club = new Club(); club.setId(clubId);
            var user = new User(); user.setId(userId);
            f.setClub(club); f.setUser(user);
            followRepository.save(f);
        }
        return followRepository.countByClub_Id(clubId);
    }

    @Transactional
    public void addComment(UUID postId, UUID userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        PostComment c = new PostComment();
        Post ref = new Post();
        ref.setId(postId);
        c.setPost(ref);

        User u = userRepository.findById(userId).orElseThrow();
        c.setAuthor(u);
        c.setContent(content.trim());
        c.setCreatedAt(OffsetDateTime.now());

        commentRepository.save(c);
    }


    public Page<ClubCardView> searchClubs(String q,
                                          Integer countryId,
                                          UUID universityId,
                                          String sort,
                                          int page, int size) {
        String query = (q == null || q.isBlank()) ? null : q.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<ClubCardProjection> proj = switch (sort == null ? "name" : sort) {
            case "followers" -> clubRepository.searchByFollowersNative(query, countryId, universityId, pageable);
            case "members"   -> clubRepository.searchByMembersNative(query, countryId, universityId, pageable);
            case "recent"    -> clubRepository.searchByRecentNative(query, countryId, universityId, pageable);
            default          -> clubRepository.searchByNameNative(query, countryId, universityId, pageable);
        };

        return proj.map(p -> new ClubCardView(
                p.getId(),
                p.getName(),
                p.getUniversityName(),
                Optional.ofNullable(p.getFollowerCount()).orElse(0L),
                Optional.ofNullable(p.getMemberCount()).orElse(0L)
        ));
    }
}