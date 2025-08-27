package com.example.clubhub4.controller;

import com.example.clubhub4.repository.NotificationRepository;
import com.example.clubhub4.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me/notifications")
@RequiredArgsConstructor
public class NotificationApiController {
    private final NotificationRepository notificationRepository;

    @GetMapping("/unread-count")
    public Map<String, Long> unread(@AuthenticationPrincipal AppUserPrincipal principal) {
        if (principal == null) return Map.of("count", 0L);
        long count = notificationRepository.countByRecipient_IdAndIsReadFalse(principal.getId());
        return Map.of("count", count);
    }
}