package com.example.clubhub4.service;
import com.example.clubhub4.dto.SignUpForm;
import com.example.clubhub4.entity.Role;
import com.example.clubhub4.entity.User;

import com.example.clubhub4.repository.UniversityRepository;
import com.example.clubhub4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;

    public User registerStudent(SignUpForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // Validate university exists
        var uni = universityRepository.findById(form.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("Selected university does not exist"));

        User u = new User();
        u.setEmail(form.getEmail().trim().toLowerCase());
        u.setFirstName(form.getFirstName().trim());
        u.setLastName(form.getLastName().trim());
        u.setPasswordHash(form.getPassword()); // plain text per your current setup
        u.setRole(Role.STUDENT);
        u.setUniversityId(uni.getId()); // set selected university

        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Email already registered");
        }
    }
}