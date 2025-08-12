package com.example.clubhub4.service;
import com.example.clubhub4.dto.CreateClubWithAdminForm;
import com.example.clubhub4.dto.UpdateClubAdminForm;
import com.example.clubhub4.dto.ClubListItem;
import com.example.clubhub4.entity.Club;
import com.example.clubhub4.entity.Role;
import com.example.clubhub4.entity.User;
import com.example.clubhub4.repository.ClubRepository;
import com.example.clubhub4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UniversityAdminService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    @Transactional
    public UUID createClubWithAdmin(UUID universityAdminUserId, CreateClubWithAdminForm form) {
        // Load current uni admin and resolve their university
        User uniAdmin = userRepository.findById(universityAdminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin account not found"));

        UUID universityId = uniAdmin.getUniversityId();
        if (universityId == null) {
            throw new IllegalStateException("Your account is not linked to a university");
        }

        // Basic validations
        if (!form.getAdminPassword().equals(form.getAdminConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByEmail(form.getAdminEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (clubRepository.existsByNameIgnoreCaseAndUniversityId(form.getClubName().trim(), universityId)) {
            throw new IllegalArgumentException("A club with this name already exists in your university");
        }

        // Create club admin user
        User clubAdmin = new User();
        clubAdmin.setEmail(form.getAdminEmail().trim().toLowerCase());
        clubAdmin.setFirstName(form.getAdminFirstName().trim());
        clubAdmin.setLastName(form.getAdminLastName().trim());
        clubAdmin.setPasswordHash(form.getAdminPassword()); // plain per your setup
        clubAdmin.setRole(Role.CLUB_ADMIN);
        clubAdmin.setUniversityId(universityId);
        clubAdmin = userRepository.save(clubAdmin);

        // Create club
        Club club = new Club();
        club.setName(form.getClubName().trim());
        club.setDescription(form.getDescription());
        club.setLogoUrl(form.getLogoUrl());
        club.setBannerUrl(form.getBannerUrl());
        club.setUniversityId(universityId);
        club.setAdmin(clubAdmin);

        try {
            club = clubRepository.save(club);
        } catch (DataIntegrityViolationException ex) {
            // In case of race condition on unique constraints
            throw new IllegalArgumentException("A club with this name already exists in your university");
        }

        return club.getId();
    }

    public Page<ClubListItem> listClubsForAdmin(UUID universityAdminUserId, int page, int size) {
        User uniAdmin = userRepository.findById(universityAdminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin account not found"));

        UUID universityId = uniAdmin.getUniversityId();
        if (universityId == null) {
            throw new IllegalStateException("Your account is not linked to a university");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return clubRepository.findListByUniversityId(universityId, pageable);
    }

    @Transactional
    public void updateClubAdminCredentials(UUID universityAdminUserId, UUID clubId, UpdateClubAdminForm form) {
        User uniAdmin = userRepository.findById(universityAdminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin account not found"));

        UUID universityId = uniAdmin.getUniversityId();
        if (universityId == null) {
            throw new IllegalStateException("Your account is not linked to a university");
        }

        Club club = clubRepository.findByIdWithAdmin(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));

        if (!universityId.equals(club.getUniversityId())) {
            throw new IllegalArgumentException("This club is not in your university");
        }

        User admin = club.getAdmin();

        // Normalize inputs
        String newEmail = form.getAdminEmail().trim().toLowerCase();
        String newFirst = form.getAdminFirstName().trim();
        String newLast = form.getAdminLastName().trim();

        // Email uniqueness (cannot take another user's email)
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, admin.getId())) {
            throw new IllegalArgumentException("Email already registered to another account");
        }

        // Optional password change
        if (form.getAdminPassword() != null && !form.getAdminPassword().isBlank()) {
            if (!form.getAdminPassword().equals(form.getAdminConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            admin.setPasswordHash(form.getAdminPassword()); // plain text by your choice
        }

        // Update profile fields
        admin.setEmail(newEmail);
        admin.setFirstName(newFirst);
        admin.setLastName(newLast);

        // Ensure role and university are correct
        admin.setRole(Role.CLUB_ADMIN);
        admin.setUniversityId(universityId);

        userRepository.save(admin);
        // No change to club.admin; we keep the same admin user (thus no risk of an admin being used by multiple clubs)
        // DB constraint unique_club_admin (admin_id unique) ensures one admin can't be attached to multiple clubs
    }




}