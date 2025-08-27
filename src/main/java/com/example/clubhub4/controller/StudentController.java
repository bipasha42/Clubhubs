package com.example.clubhub4.controller;

import com.example.clubhub4.dto.FeedCardView;
import com.example.clubhub4.service.StudentFeedService;
import com.example.clubhub4.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentFeedService feedService;

    @GetMapping("/feed")
    public String feed(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<FeedCardView> posts = feedService.getFeed(principal.getId(), page, size);
        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "student/feed";
    }

}