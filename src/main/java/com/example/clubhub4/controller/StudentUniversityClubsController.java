package com.example.clubhub4.controller;

import com.example.clubhub4.entity.ApplicationStatus;
import com.example.clubhub4.dto.ClubCardView;
import com.example.clubhub4.service.ExploreService;
import com.example.clubhub4.repository.*;
import com.example.clubhub4.security.AppUserPrincipal;
import com.example.clubhub4.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/student/university/clubs")
@RequiredArgsConstructor
public class StudentUniversityClubsController {

    private final ExploreService exploreService;                // has searchClubs(...) and followedClubIds(...)
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubApplicationRepository clubApplicationRepository;
    private final ClubMemberRepository clubMemberRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(defaultValue = "name") String sort,  // name | followers | members | recent
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       HttpServletRequest request,
                       Model model) {

        // 1) Validate user and university
        User user = userRepository.findById(principal.getId()).orElse(null);
        boolean noUniversity = (user == null || user.getUniversityId() == null);
        model.addAttribute("noUniversity", noUniversity);
        if (noUniversity) {
            return "student/university-clubs";
        }

        // 2) Load clubs for user's university (paged)
        UUID universityId = user.getUniversityId();
        Page<ClubCardView> clubs = exploreService.searchClubs(null, null, universityId, sort, page, size);

        // 3) Build "back" URL so Follow/Apply forms can redirect here
        String back = request.getRequestURI();
        if (request.getQueryString() != null) back += "?" + request.getQueryString();

        // Defaults
        Set<UUID> eligibleApplyIds = Set.of();
        Map<UUID, ApplicationStatus> applicationStatuses = Map.of();
        Set<UUID> cancelEligibleIds = Set.of();
        Set<UUID> reapplyEligibleIds = Set.of();
        Set<UUID> memberClubIds = Set.of();
        Set<UUID> followedIds = Set.of();  // <-- THIS is the piece you were missing

        // 4) Compute per-card statuses if we have clubs
        if (!clubs.isEmpty()) {
            List<UUID> ids = clubs.getContent().stream().map(ClubCardView::id).toList();

            // 4a) Who can apply (same university, not member, no existing application)
            eligibleApplyIds = clubRepository.findEligibleApplyClubIds(principal.getId(), ids);

            // 4b) Current applications -> status map + cancel/reapply sets
            var apps = clubApplicationRepository.findByUserAndClubs(principal.getId(), ids);
            Map<UUID, ApplicationStatus> statusMap = new HashMap<>();
            Set<UUID> cancel = new HashSet<>();
            Set<UUID> reapply = new HashSet<>();
            for (var a : apps) {
                UUID cid = a.getClub().getId();
                statusMap.put(cid, a.getStatus());
                if (a.getStatus() == ApplicationStatus.PENDING) cancel.add(cid);
                if (a.getStatus() == ApplicationStatus.REJECTED) reapply.add(cid);
            }
            applicationStatuses = statusMap;
            cancelEligibleIds = cancel;
            reapplyEligibleIds = reapply;

            // 4c) Membership flags
            memberClubIds = clubMemberRepository.findMemberClubIds(principal.getId(), ids);

            // 4d) FOLLOWED CLUBS (used by the button text to show Follow / Following)
            followedIds = exploreService.followedClubIds(principal.getId(), ids);  // <-- add this
        }

        // 5) Model
        model.addAttribute("clubs", clubs);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("back", back);

        model.addAttribute("eligibleApplyIds", eligibleApplyIds);
        model.addAttribute("applicationStatuses", applicationStatuses);
        model.addAttribute("cancelEligibleIds", cancelEligibleIds);
        model.addAttribute("reapplyEligibleIds", reapplyEligibleIds);
        model.addAttribute("memberClubIds", memberClubIds);
        model.addAttribute("followedIds", followedIds);  // <-- add to the model

        return "student/university-clubs";
    }
}
