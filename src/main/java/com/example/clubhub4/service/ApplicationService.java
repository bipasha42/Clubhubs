package com.example.clubhub4.service;

import com.example.clubhub4.entity.*;
import com.example.clubhub4.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubApplicationRepository clubApplicationRepository;

    public boolean canApply(UUID userId, UUID clubId) {
        if (userId == null) return false;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.STUDENT) return false;

        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) return false;

        if (user.getUniversityId() == null || !user.getUniversityId().equals(club.getUniversityId())) return false;
        if (clubMemberRepository.existsByClub_IdAndUser_Id(clubId, userId)) return false;
        if (clubApplicationRepository.existsByClub_IdAndUser_Id(clubId, userId)) return false;
        return true;
    }

    public Optional<ApplicationStatus> applicationStatus(UUID userId, UUID clubId) {
        return clubApplicationRepository.findByClub_IdAndUser_Id(clubId, userId).map(ClubApplication::getStatus);
    }

    @Transactional
    public UUID apply(UUID userId, UUID clubId, String text) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getRole() != Role.STUDENT) throw new AccessDeniedException("Only students can apply");
        Club club = clubRepository.findById(clubId).orElseThrow();

        // University match
        if (user.getUniversityId() == null || !user.getUniversityId().equals(club.getUniversityId())) {
            throw new AccessDeniedException("You can only apply to clubs in your university");
        }
        // Not a member already
        if (clubMemberRepository.existsByClub_IdAndUser_Id(clubId, userId)) {
            throw new IllegalStateException("You are already a member of this club");
        }
        // Unique application
        if (clubApplicationRepository.existsByClub_IdAndUser_Id(clubId, userId)) {
            throw new IllegalStateException("You already applied to this club");
        }

        ClubApplication app = new ClubApplication();
        app.setClub(club);
        app.setUser(user);
        app.setApplicationText((text == null || text.isBlank()) ? "I'd like to join this club." : text.trim());
        app.setStatus(ApplicationStatus.PENDING);
        app.setAppliedAt(OffsetDateTime.now());

        app = clubApplicationRepository.save(app);
        return app.getId();
    }
}