package com.example.clubhub4.service;

import com.example.clubhub4.entity.Club;
import com.example.clubhub4.entity.Post;
import com.example.clubhub4.entity.User;
import com.example.clubhub4.repository.ClubMemberRepository;
import com.example.clubhub4.repository.PostRepository;
import com.example.clubhub4.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class MemberPostingService {

    private final ClubMemberRepository clubMemberRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public boolean canPost(UUID clubId, UUID userId) {
        return clubId != null && userId != null
                && clubMemberRepository.existsByClub_IdAndUser_Id(clubId, userId);
    }

    @Transactional
    public UUID createPostAsMember(UUID userId, UUID clubId, String content) {
        if (userId == null) throw new AccessDeniedException("Login required");
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (!canPost(clubId, userId)) {
            throw new AccessDeniedException("You must be a club member to post");
        }

        User author = userRepository.findById(userId).orElseThrow();
        Club clubRef = entityManager.getReference(Club.class, clubId);

        Post p = new Post();
        //p.setId(UUID.randomUUID());
        p.setAuthor(author);
        p.setClub(clubRef);
        p.setContent(content.trim());
//        p.setCreatedAt(OffsetDateTime.now());
//        p.setUpdatedAt(OffsetDateTime.now());


        p = postRepository.save(p);
        return p.getId();
    }
}