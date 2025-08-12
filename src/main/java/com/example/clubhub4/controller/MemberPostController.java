package com.example.clubhub4.controller;

import com.example.clubhub4.service.MemberPostingService;
import com.example.clubhub4.dto.MyClubOption;
import com.example.clubhub4.repository.ClubMemberRepository;
import com.example.clubhub4.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/me/posts")
public class MemberPostController {

    private final ClubMemberRepository clubMemberRepository;
    private final MemberPostingService memberPostingService;

    // Show "create post" page with user's clubs
    @GetMapping("/new")
    public String newPost(@AuthenticationPrincipal AppUserPrincipal principal,
                          @RequestParam(value = "clubId", required = false) UUID preselectClubId,
                          Model model) {
        List<MyClubOption> clubs = clubMemberRepository.findClubOptionsByUserId(principal.getId());
        model.addAttribute("clubs", clubs);
        model.addAttribute("preselectClubId", preselectClubId);
        return "student/post-new";
    }

    // Handle submit
    @PostMapping
    public String create(@AuthenticationPrincipal AppUserPrincipal principal,
                         @RequestParam("clubId") UUID clubId,
                         @RequestParam("content") String content) {
        try {
            UUID postId = memberPostingService.createPostAsMember(principal.getId(), clubId, content);
            return "redirect:/clubs/" + clubId + "/posts/" + postId;
        } catch (AccessDeniedException ex) {
            String msg = URLEncoder.encode("You must be a member of the selected club to post.", StandardCharsets.UTF_8);
            return "redirect:/me/posts/new?error=" + msg;
        } catch (IllegalArgumentException ex) {
            String msg = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/me/posts/new?error=" + msg + "&clubId=" + clubId;
        }
    }
}