package com.example.clubhub4.controller;

import com.example.clubhub4.service.*;
import com.example.clubhub4.dto.*;
import com.example.clubhub4.security.AppUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PublicClubController {

    private final ExploreService exploreService;
    private final MemberPostingService memberPostingService;
    private final AuthorPostService authorPostService;
    private final ApplicationService applicationService;
    private final ImageStorageService imageStorageService;




    @GetMapping("/clubs/{clubId}")
    public String club(@PathVariable UUID clubId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @AuthenticationPrincipal AppUserPrincipal principal,
                       HttpServletRequest request,
                       Model model) {
        var clubCard = exploreService.getClubCard(clubId);
        Page<PostCardView> posts = exploreService.listClubPosts(clubId, page, size);
        boolean isFollowing = principal != null && exploreService.isFollowing(clubId, principal.getId());

        boolean canApply = principal != null && applicationService.canApply(principal.getId(), clubId);
        var appStatus = (principal != null) ? applicationService.applicationStatus(principal.getId(), clubId) : java.util.Optional.empty();
        boolean canReapply = principal != null && applicationService.canReapply(principal.getId(), clubId);
        boolean canCancel = principal != null && applicationService.canCancel(principal.getId(), clubId);

        model.addAttribute("canApplyToClub", canApply);
        model.addAttribute("applicationStatus", appStatus.orElse(null));
        model.addAttribute("canReapply", canReapply);
        model.addAttribute("canCancel", canCancel);


        String back = request.getRequestURI();
        if (request.getQueryString() != null) back += "?" + request.getQueryString();

        model.addAttribute("club", clubCard);
        model.addAttribute("posts", posts);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("back", back);


        return "club/club";
    }



    @PostMapping("/posts/{postId}/like")
    public String like(@PathVariable UUID postId,
                       @AuthenticationPrincipal AppUserPrincipal principal) {
        var userId = principal.getId();
        var post = exploreService.getPostDetails(postId, userId);
        exploreService.toggleLike(postId, userId);
        return "redirect:/clubs/" + post.clubId() + "/posts/" + post.postId();
    }

    @PostMapping("/posts/{postId}/comments")
    public String comment(@PathVariable UUID postId,
                          @RequestParam String content,
                          @AuthenticationPrincipal AppUserPrincipal principal) {
        var userId = principal.getId();
        var post = exploreService.getPostDetails(postId, userId);
        exploreService.addComment(postId, userId, content);
        return "redirect:/clubs/" + post.clubId() + "/posts/" + post.postId() + "#comments";
    }
    @PostMapping("/clubs/{clubId}/follow")
    public String toggleFollow(@PathVariable UUID clubId,
                               @RequestParam(value = "back", required = false) String back,
                               @AuthenticationPrincipal AppUserPrincipal principal) {
        exploreService.toggleFollow(clubId, principal.getId());

        // Safe redirect back to same page if provided; else to club page
        if (back != null && back.startsWith("/") && !back.contains("://")) {
            return "redirect:" + back;
        }
        return "redirect:/clubs/" + clubId;
    }

//    @GetMapping("/clubs/{clubId}/posts/{postId}")
//    public String post(@PathVariable UUID clubId,
//                       @PathVariable UUID postId,
//                       @AuthenticationPrincipal AppUserPrincipal principal,
//                       jakarta.servlet.http.HttpServletRequest request,
//                       Model model) {
//        var post = exploreService.getPostDetails(postId, principal != null ? principal.getId() : null);
//        if (!post.clubId().equals(clubId)) {
//            return "redirect:/clubs/" + post.clubId() + "/posts/" + post.postId();
//        }
//        var comments = exploreService.getComments(postId);
//
//        boolean isClubAdmin = principal != null && exploreService.isAdminOfClub(clubId, principal.getId());
//
//        String back = request.getRequestURI();
//        if (request.getQueryString() != null) back += "?" + request.getQueryString();
//
//        model.addAttribute("post", post);
//        model.addAttribute("comments", comments);
//        model.addAttribute("isClubAdmin", isClubAdmin);
//        model.addAttribute("back", back);
//        return "club/post";
//    }
    // Create post (member-only)
    @PostMapping("/clubs/{clubId}/posts")
    public String createMemberPost(@PathVariable UUID clubId,
                                   @RequestParam(value = "content", required = false) String content,
                                   @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                                   @AuthenticationPrincipal AppUserPrincipal principal) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageStorageService.saveImage(image);
        }
        UUID postId = memberPostingService.createPostAsMember(principal.getId(), clubId, content, imageUrl);
        return "redirect:/clubs/" + clubId + "/posts/" + postId;
    }

    // Post details (add isAuthor + isClubAdmin)
    @GetMapping("/clubs/{clubId}/posts/{postId}")
    public String post(@PathVariable UUID clubId,
                       @PathVariable UUID postId,
                       @AuthenticationPrincipal AppUserPrincipal principal,
                       HttpServletRequest request,
                       Model model) {
        var post = exploreService.getPostDetails(postId, principal != null ? principal.getId() : null);
        if (!post.clubId().equals(clubId)) {
            return "redirect:/clubs/" + post.clubId() + "/posts/" + post.postId();
        }
        var comments = exploreService.getComments(postId);
        boolean isClubAdmin = principal != null && exploreService.isAdminOfClub(clubId, principal.getId());
        boolean isAuthor = principal != null && exploreService.isPostAuthor(postId, principal.getId());

        String back = request.getRequestURI();
        if (request.getQueryString() != null) back += "?" + request.getQueryString();

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("isClubAdmin", isClubAdmin);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("back", back);
        return "club/post";
    }

    // Edit page (author only)
    @GetMapping("/clubs/{clubId}/posts/{postId}/edit")
    public String editPost(@PathVariable UUID clubId,
                           @PathVariable UUID postId,
                           @AuthenticationPrincipal AppUserPrincipal principal,
                           Model model) {
        var post = authorPostService.requireAuthorOwnedPost(principal.getId(), clubId, postId);
        model.addAttribute("postId", postId);
        model.addAttribute("clubId", clubId);
        model.addAttribute("content", post.getContent());
        model.addAttribute("currentImageUrl", post.getImageUrl()); // show current image
        return "club/post-edit";
    }

    @PostMapping("/clubs/{clubId}/posts/{postId}")
    public String saveEdit(@PathVariable UUID clubId,
                           @PathVariable UUID postId,
                           @RequestParam("content") String content,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                           @AuthenticationPrincipal AppUserPrincipal principal) {

        String newUrl = null;
        if (image != null && !image.isEmpty()) {
            newUrl = imageStorageService.saveImage(image);
        }

        authorPostService.updatePostAsAuthor(principal.getId(), clubId, postId, content, newUrl, removeImage);
        return "redirect:/clubs/" + clubId + "/posts/" + postId + "?updated";
    }


    // Delete post (author only)
    @PostMapping("/clubs/{clubId}/posts/{postId}/delete")
    public String deletePost(@PathVariable UUID clubId,
                             @PathVariable UUID postId,
                             @AuthenticationPrincipal AppUserPrincipal principal) {
        authorPostService.deletePostAsAuthor(principal.getId(), clubId, postId);
        return "redirect:/clubs/" + clubId + "?postDeleted";
    }

    // Delete a comment (author of the post)
    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteCommentAsAuthor(@PathVariable UUID postId,
                                        @PathVariable UUID commentId,
                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        authorPostService.deleteCommentAsPostAuthor(principal.getId(), postId, commentId);
        // We need the clubId to redirect; easiest: reload details
        var pd = exploreService.getPostDetails(postId, principal.getId());
        return "redirect:/clubs/" + pd.clubId() + "/posts/" + postId + "?commentDeleted#comments";
    }

    @PostMapping("/clubs/{clubId}/apply")
    public String apply(@PathVariable UUID clubId,
                        @RequestParam(value = "applicationText", required = false) String text,
                        @AuthenticationPrincipal com.example.clubhub4.security.AppUserPrincipal principal) {
        applicationService.apply(principal.getId(), clubId, text);
        return "redirect:/clubs/" + clubId + "?applied";
    }
    @PostMapping("/clubs/{clubId}/apply/cancel")
    public String cancelApplication(@PathVariable UUID clubId,
                                    @AuthenticationPrincipal com.example.clubhub4.security.AppUserPrincipal principal) {
        applicationService.cancelPending(principal.getId(), clubId);
        return "redirect:/clubs/" + clubId + "?success=Application+cancelled";
    }

    @PostMapping("/clubs/{clubId}/apply/reapply")
    public String reapply(@PathVariable UUID clubId,
                          @RequestParam(value = "applicationText", required = false) String text,
                          @AuthenticationPrincipal com.example.clubhub4.security.AppUserPrincipal principal) {
        applicationService.reapply(principal.getId(), clubId, text);
        return "redirect:/clubs/" + clubId + "?success=Application+resubmitted";
    }
}