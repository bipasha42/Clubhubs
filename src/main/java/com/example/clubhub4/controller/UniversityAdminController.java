package com.example.clubhub4.controller;

import com.example.clubhub4.dto.ClubDisplay;
import com.example.clubhub4.dto.ClubListItem;
import com.example.clubhub4.security.AppUserPrincipal;
import com.example.clubhub4.dto.CreateClubWithAdminForm;
import com.example.clubhub4.service.UniversityAdminService;
import com.example.clubhub4.dto.UpdateClubAdminForm;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/university")
@RequiredArgsConstructor
public class UniversityAdminController {

    private final UniversityAdminService universityAdminService;

    // List clubs
    @GetMapping("/clubs")
    public String listClubs(@AuthenticationPrincipal AppUserPrincipal principal,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        Page<ClubListItem> clubs = universityAdminService.listClubsForAdmin(principal.getId(), page, size);
        model.addAttribute("clubs", clubs);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "university/clubs";
    }

    // New club form
    @GetMapping("/clubs/new")
    public String newClubForm(Model model) {
        model.addAttribute("form", new CreateClubWithAdminForm());
        return "university/new-club";
    }

    // Create club + admin
    @PostMapping("/clubs")
    public String createClub(@AuthenticationPrincipal AppUserPrincipal principal,
                             @Valid @ModelAttribute("form") CreateClubWithAdminForm form,
                             BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return "university/new-club";
        }
        try {
            universityAdminService.createClubWithAdmin(principal.getId(), form);
            return "redirect:/university/clubs/new?success";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            return "university/new-club";
        }
    }

    // Manage club (prefilled admin credentials)
    @GetMapping("/clubs/{clubId}/manage")
    public String manageClub(@AuthenticationPrincipal AppUserPrincipal principal,
                             @PathVariable UUID clubId,
                             Model model) {
        try {
            // banner/title
            ClubDisplay club = universityAdminService.getClubDisplay(principal.getId(), clubId);
            model.addAttribute("club", club);
            model.addAttribute("clubId", clubId);

            // prefilled form (email, first/last)
            UpdateClubAdminForm form = universityAdminService.getAdminForm(principal.getId(), clubId);
            model.addAttribute("form", form); // password fields intentionally blank
            return "university/manage-club";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Fall back to list with an error toast
            model.addAttribute("error", ex.getMessage());
            return "redirect:/university/clubs";
        }
    }

    // Update club admin credentials
    @PostMapping("/clubs/{clubId}/manage")
    public String updateClubAdmin(@AuthenticationPrincipal AppUserPrincipal principal,
                                  @PathVariable UUID clubId,
                                  @Valid @ModelAttribute("form") UpdateClubAdminForm form,
                                  BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            // keep banner visible on error
            ClubDisplay club = universityAdminService.getClubDisplay(principal.getId(), clubId);
            model.addAttribute("club", club);
            model.addAttribute("clubId", clubId);
            return "university/manage-club";
        }
        try {
            universityAdminService.updateClubAdminCredentials(principal.getId(), clubId, form);
            return "redirect:/university/clubs/" + clubId + "/manage?success";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ClubDisplay club = universityAdminService.getClubDisplay(principal.getId(), clubId);
            model.addAttribute("club", club);
            model.addAttribute("clubId", clubId);
            model.addAttribute("error", ex.getMessage());
            return "university/manage-club";
        }
    }
}