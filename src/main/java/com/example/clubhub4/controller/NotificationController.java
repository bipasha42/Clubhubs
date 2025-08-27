package com.example.clubhub4.controller;

import com.example.clubhub4.service.NotificationService;
import com.example.clubhub4.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.clubhub4.dto.NotificationView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/notifications")
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<NotificationView> notifications = service.list(principal.getId(), page, size);
        model.addAttribute("notifications", notifications);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "student/notifications";
    }

    @PostMapping("/{id}/read")
    public String markOne(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable UUID id,
                          @RequestParam(value = "back", required = false) String back) {
        service.markOneRead(principal.getId(), id);
        return (back != null && back.startsWith("/") && !back.contains("://"))
                ? "redirect:" + back
                : "redirect:/student/notifications";
    }

    @PostMapping("/read-all")
    public String markAll(@AuthenticationPrincipal AppUserPrincipal principal) {
        service.markAllRead(principal.getId());
        return "redirect:/student/notifications";
    }
}