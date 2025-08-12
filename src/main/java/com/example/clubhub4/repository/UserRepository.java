package com.example.clubhub4.repository;

import com.example.clubhub4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}