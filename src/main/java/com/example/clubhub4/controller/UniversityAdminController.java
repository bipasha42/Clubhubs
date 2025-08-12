package com.example.clubhub4.controller;

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

    @GetMapping("/clubs")
    public String listClubs(@AuthenticationPrincipal AppUserPrincipal principal,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size,
                            Model model) {
        Page<?> clubs = universityAdminService.listClubsForAdmin(principal.getId(), page, size);
        model.addAttribute("clubs", clubs);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "university/clubs";
    }

    // NEW: Manage a club's admin credentials
    @GetMapping("/clubs/{id}/manage")
    public String manageClub(@AuthenticationPrincipal AppUserPrincipal principal,
                             @PathVariable("id") UUID clubId,
                             Model model) {
        // Prefill from service by leveraging the list projection or do a fetch; simpler: fetch via list page or re-query
        var page = universityAdminService.listClubsForAdmin(principal.getId(), 0, 1);
        // For simplicity, we’ll get the admin details via a service fetch on the club itself
        // To avoid adding another service method, we can accept a small duplication here:
        // A minimal form with required fields; the POST will validate ownership again.
        model.addAttribute("clubId", clubId);
        model.addAttribute("form", new UpdateClubAdminForm());
        return "university/manage-club";
    }

    @PostMapping("/clubs/{id}/manage")
    public String updateClubAdmin(@AuthenticationPrincipal AppUserPrincipal principal,
                                  @PathVariable("id") UUID clubId,
                                  @Valid @ModelAttribute("form") UpdateClubAdminForm form,
                                  BindingResult binding,
                                  Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("clubId", clubId);
            return "university/manage-club";
        }
        try {
            universityAdminService.updateClubAdminCredentials(principal.getId(), clubId, form);
            return "redirect:/university/clubs/{id}/manage?success";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("clubId", clubId);
            model.addAttribute("error", ex.getMessage());
            return "university/manage-club";
        }
    }



    @GetMapping("/clubs/new")
    public String newClubForm(Model model) {
        model.addAttribute("form", new CreateClubWithAdminForm());
        return "university/new-club";
    }

    @PostMapping("/clubs")
    public String createClubAndAdmin(@AuthenticationPrincipal AppUserPrincipal principal,
                                     @Valid @ModelAttribute("form") CreateClubWithAdminForm form,
                                     BindingResult binding,
                                     Model model) {
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



}