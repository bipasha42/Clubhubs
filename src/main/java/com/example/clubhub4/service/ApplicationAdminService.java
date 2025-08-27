package com.example.clubhub4.service;


import com.example.clubhub4.dto.ApplicationItem;
import com.example.clubhub4.entity.*;
import com.example.clubhub4.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationAdminService {

    private final ClubRepository clubRepository;
    private final ClubApplicationRepository clubApplicationRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<ApplicationItem> listForMyClub(UUID adminUserId, int page, int size) {
        Club club = clubRepository.findByAdmin_Id(adminUserId)
                .orElseThrow(() -> new IllegalStateException("No club assigned to your admin account"));
        return clubApplicationRepository.listForClub(club.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public void updateStatus(UUID adminUserId, UUID applicationId, ApplicationStatus newStatus) {
        ClubApplication app = clubApplicationRepository.findByIdForAdmin(applicationId, adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Only allow processing from PENDING -> ACCEPTED/REJECTED exactly once
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("This application has already been processed.");
        }
        if (newStatus == ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Invalid status transition");
        }

        app.setStatus(newStatus);
        app.setProcessedAt(OffsetDateTime.now());
        User admin = userRepository.findById(adminUserId).orElseThrow();
        app.setProcessedBy(admin);

        if (newStatus == ApplicationStatus.ACCEPTED) {
            // Add membership (idempotent)
            ClubMember m = new ClubMember();
            m.setClub(app.getClub());
            m.setUser(app.getUser());
            m.setMemberSince(OffsetDateTime.now());
            try {
                clubMemberRepository.save(m);
            } catch (DataIntegrityViolationException ignore) {}
        } else if (newStatus == ApplicationStatus.REJECTED) {
            // Ensure membership is removed if it exists (cleanup)
            clubMemberRepository.deleteByClub_IdAndUser_Id(app.getClub().getId(), app.getUser().getId());
        }

        clubApplicationRepository.save(app);

        // Notify applicant
        String msg = switch (newStatus) {
            case ACCEPTED -> "Your application to join " + app.getClub().getName() + " has been ACCEPTED.";
            case REJECTED -> "Your application to join " + app.getClub().getName() + " has been REJECTED.";
            default -> "Your application to join " + app.getClub().getName() + " is PENDING.";
        };
        Notification n = new Notification();
        n.setApplication(app);
        n.setRecipient(app.getUser());
        n.setMessage(msg);
        notificationRepository.save(n);
    }
}