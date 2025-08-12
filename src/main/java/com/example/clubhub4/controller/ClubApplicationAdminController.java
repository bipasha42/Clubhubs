package com.example.clubhub4.controller;

import com.example.clubhub4.entity.ApplicationStatus;
import com.example.clubhub4.service.ApplicationAdminService;
import com.example.clubhub4.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/club/applications")
@RequiredArgsConstructor
public class ClubApplicationAdminController {

    private final ApplicationAdminService service;

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<?> apps = service.listForMyClub(principal.getId(), page, size);
        model.addAttribute("apps", apps);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "clubadmin/applications";
    }

    @PostMapping("/{appId}/status")
    public String update(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable java.util.UUID appId,
                         @RequestParam ApplicationStatus status) {
        service.updateStatus(principal.getId(), appId, status);
        return "redirect:/club/applications";
    }
}