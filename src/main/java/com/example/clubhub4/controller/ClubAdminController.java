package com.example.clubhub4.controller;

import com.example.clubhub4.service.ClubAdminService;
import com.example.clubhub4.dto.MemberListItem;
import com.example.clubhub4.dto.PostForm;
import com.example.clubhub4.dto.PostCardView;
import com.example.clubhub4.security.AppUserPrincipal;
import com.example.clubhub4.service.ImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubAdminController {

    private final ClubAdminService service;
    private final ImageStorageService imageStorageService;

    // 1) Admin feed — only own club posts
    @GetMapping("/feed")
    public String feed(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        var club = service.getMyClubCard(principal.getId());
        Page<PostCardView> posts = service.listMyFeed(principal.getId(), page, size);
        model.addAttribute("club", club);
        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "clubadmin/feed";
    }

    // 2) Create/Edit/Delete posts
    @GetMapping("/posts/new")
    public String newPost(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("form", new PostForm());
        // Add club for banner
        var club = service.getMyClubCard(principal.getId());
        model.addAttribute("club", club);
        return "clubadmin/post-form";
    }

    @PostMapping("/posts")
    public String create(@AuthenticationPrincipal AppUserPrincipal principal,
                         @Valid @ModelAttribute("form") PostForm form,
                         BindingResult binding,
                         @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        if (binding.hasErrors()) return "clubadmin/post-form";
        String url = (image != null && !image.isEmpty()) ? imageStorageService.saveImage(image) : null;
        UUID id = service.createPost(principal.getId(), form, url);
        return "redirect:/club/posts/" + id + "/edit?created";
    }

    @GetMapping("/posts/{id}/edit")
    public String edit(@AuthenticationPrincipal AppUserPrincipal principal,
                       @PathVariable("id") UUID id,
                       Model model) {
        var post = service.getMyClubPost(principal.getId(), id);
        PostForm form = new PostForm();
        form.setContent(post.getContent());
        model.addAttribute("form", form);
        model.addAttribute("postId", id);
        // Add current image if you show it in the form
        model.addAttribute("currentImageUrl", post.getImageUrl());
        // Add club for banner
        var club = service.getMyClubCard(principal.getId());
        model.addAttribute("club", club);
        return "clubadmin/post-form";
    }



    @PostMapping("/posts/{id}")
    public String update(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable("id") UUID id,
                         @Valid @ModelAttribute("form") PostForm form,
                         BindingResult binding,
                         @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                         @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage) {
        if (binding.hasErrors()) {
            return "clubadmin/post-form";
        }
        String url = (image != null && !image.isEmpty()) ? imageStorageService.saveImage(image) : null;
        service.updatePost(principal.getId(), id, form, url, removeImage);
        return "redirect:/club/feed?updated";
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable("id") UUID id) {
        service.deletePost(principal.getId(), id);
        return "redirect:/club/feed?deleted";
    }

    // 4) Members list + remove
    @GetMapping("/members")
    public String members(@AuthenticationPrincipal AppUserPrincipal principal,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {
        var club = service.getMyClubCard(principal.getId());
        Page<MemberListItem> members = service.listMembers(principal.getId(), page, size);
        model.addAttribute("club", club);
        model.addAttribute("members", members);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "clubadmin/members";
    }

    @PostMapping("/members/{userId}/remove")
    public String removeMember(@AuthenticationPrincipal AppUserPrincipal principal,
                               @PathVariable("userId") UUID memberUserId,
                               Model model) {
        try {
            service.removeMember(principal.getId(), memberUserId);
            return "redirect:/club/members?removed";
        } catch (IllegalArgumentException ex) {
            return "redirect:/club/members?error=" + ex.getMessage().replace(' ', '+');
        }
    }
    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@AuthenticationPrincipal AppUserPrincipal principal,
                                @PathVariable("id") UUID commentId,
                                @RequestParam(value = "back", required = false) String back) {
        var result = service.deleteComment(principal.getId(), commentId);

        // Prefer going back to the same page (safe local path only), else to the post page
        String target = (back != null && back.startsWith("/") && !back.contains("://"))
                ? back
                : "/clubs/" + result.clubId() + "/posts/" + result.postId();

        // Add a flag for UI feedback and jump to comments
        return "redirect:" + target + (target.contains("?") ? "&" : "?") + "commentDeleted#comments";
    }
}