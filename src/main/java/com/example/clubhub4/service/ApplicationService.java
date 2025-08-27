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

        // No existing application or it's been deleted; if exists and REJECTED, use canReapply()
        return clubApplicationRepository.findByClub_IdAndUser_Id(clubId, userId).isEmpty();
    }

    public Optional<ApplicationStatus> applicationStatus(UUID userId, UUID clubId) {
        return clubApplicationRepository.findByClub_IdAndUser_Id(clubId, userId).map(ClubApplication::getStatus);
    }

    public boolean canReapply(UUID userId, UUID clubId) {
        return clubApplicationRepository
                .findByClub_IdAndUser_IdAndStatus(clubId, userId, ApplicationStatus.REJECTED)
                .isPresent();
    }

    public boolean canCancel(UUID userId, UUID clubId) {
        return clubApplicationRepository
                .findByClub_IdAndUser_IdAndStatus(clubId, userId, ApplicationStatus.PENDING)
                .isPresent();
    }

    @Transactional
    public UUID apply(UUID userId, UUID clubId, String text) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getRole() != Role.STUDENT) throw new AccessDeniedException("Only students can apply");
        Club club = clubRepository.findById(clubId).orElseThrow();

        if (user.getUniversityId() == null || !user.getUniversityId().equals(club.getUniversityId())) {
            throw new AccessDeniedException("You can only apply to clubs in your university");
        }
        if (clubMemberRepository.existsByClub_IdAndUser_Id(clubId, userId)) {
            throw new IllegalStateException("You are already a member of this club");
        }
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

    @Transactional
    public boolean cancelPending(UUID userId, UUID clubId) {
        // Only pending apps can be cancelled; delete row to free up re-apply
        long deleted = clubApplicationRepository.deleteByClub_IdAndUser_IdAndStatus(
                clubId, userId, ApplicationStatus.PENDING
        );
        return deleted > 0;
    }

    @Transactional
    public void reapply(UUID userId, UUID clubId, String text) {
        // Re-open a REJECTED application into PENDING (same row; keeps unique constraint)
        ClubApplication app = clubApplicationRepository
                .findByClub_IdAndUser_IdAndStatus(clubId, userId, ApplicationStatus.REJECTED)
                .orElseThrow(() -> new IllegalStateException("No rejected application to reapply"));

        app.setStatus(ApplicationStatus.PENDING);
        app.setAppliedAt(OffsetDateTime.now());
        app.setProcessedAt(null);
        app.setProcessedBy(null);
        if (text != null && !text.isBlank()) {
            app.setApplicationText(text.trim());
        }
        clubApplicationRepository.save(app);
    }
}