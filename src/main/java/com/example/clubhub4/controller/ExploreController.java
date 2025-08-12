package com.example.clubhub4.controller;

import com.example.clubhub4.dto.ClubCardView;
import com.example.clubhub4.service.ExploreService;
import com.example.clubhub4.repository.*;
import com.example.clubhub4.security.AppUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/explore")
public class ExploreController {

    private final ExploreService exploreService;
    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final ClubRepository clubRepository;
    private final ClubApplicationRepository clubApplicationRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;

    @GetMapping("/clubs")
    public String clubs(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer countryId,
                        @RequestParam(required = false) UUID universityId,
                        @RequestParam(defaultValue = "name") String sort, // name | followers | members | recent
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @AuthenticationPrincipal AppUserPrincipal principal,
                        HttpServletRequest request,
                        Model model) {

        Page<ClubCardView> clubs = exploreService.searchClubs(q, countryId, universityId, sort, page, size);

        Set<UUID> followedIds = Set.of();
        if (principal != null && !clubs.isEmpty()) {
            var ids = clubs.getContent().stream().map(ClubCardView::id).toList();
            followedIds = exploreService.followedClubIds(principal.getId(), ids);
        }

        // Build back URL (path + query) for the follow buttons
        String back = request.getRequestURI();
        if (request.getQueryString() != null) back += "?" + request.getQueryString();

        // Dropdowns
        var countries = countryRepository.findAllByOrderByNameAsc();
        var universities = (countryId != null)
                ? universityRepository.findByCountryIdOrderByNameAsc(countryId)
                : List.of(); // keep empty until country selected

        java.util.Set<java.util.UUID> eligibleApplyIds = java.util.Set.of();
        java.util.Map<java.util.UUID, com.example.clubhub4.entity.ApplicationStatus> appStatuses = java.util.Map.of();
        java.util.Set<java.util.UUID> memberClubIds = java.util.Set.of();

        if (principal != null) {
            var user = userRepository.findById(principal.getId()).orElse(null);
            if (user != null && user.getRole() == com.example.clubhub4.entity.Role.STUDENT && !clubs.isEmpty()) {
                var ids = clubs.getContent().stream().map(com.example.clubhub4.dto.ClubCardView::id).toList();
                eligibleApplyIds = clubRepository.findEligibleApplyClubIds(principal.getId(), ids);
                var apps = clubApplicationRepository.findByUserAndClubs(principal.getId(), ids);
                appStatuses = apps.stream().collect(java.util.stream.Collectors.toMap(a -> a.getClub().getId(), com.example.clubhub4.entity.ClubApplication::getStatus, (a,b)->a));
                memberClubIds = clubMemberRepository.findMemberClubIds(principal.getId(), ids);
            }
        }


        model.addAttribute("clubs", clubs);
        model.addAttribute("followedIds", followedIds);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("q", q);
        model.addAttribute("countryId", countryId);
        model.addAttribute("universityId", universityId);
        model.addAttribute("sort", sort);
        model.addAttribute("back", back);
        model.addAttribute("countries", countries);
        model.addAttribute("universities", universities);
        model.addAttribute("eligibleApplyIds", eligibleApplyIds);
        model.addAttribute("applicationStatuses", appStatuses);
        model.addAttribute("memberClubIds", memberClubIds);

        return "explore/clubs";
    }
}