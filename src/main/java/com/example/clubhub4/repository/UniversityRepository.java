package com.example.clubhub4.repository;

import com.example.clubhub4.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UniversityRepository extends JpaRepository<University, UUID> {
    List<University> findByCountryIdOrderByNameAsc(Integer countryId);
    List<University> findAllByOrderByNameAsc();
}