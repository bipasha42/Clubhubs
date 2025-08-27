package com.example.clubhub4.controller;

import com.example.clubhub4.security.AppUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeController {

    @GetMapping("/me/home")
    public String myHome(@AuthenticationPrincipal AppUserPrincipal principal, Authentication auth) {
        if (principal == null) return "redirect:/login";
        if (has(auth, "ROLE_UNIVERSITY_ADMIN")) return "redirect:/university/clubs";
        if (has(auth, "ROLE_CLUB_ADMIN"))       return "redirect:/club/feed";
        if (has(auth, "ROLE_STUDENT"))          return "redirect:/student/feed";
        return "redirect:/";
    }

    private boolean has(Authentication auth, String role) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (role.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}