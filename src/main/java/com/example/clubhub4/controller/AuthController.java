package com.example.clubhub4.controller;

import com.example.clubhub4.dto.SignUpForm;
import com.example.clubhub4.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.clubhub4.security.AppUserPrincipal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal AppUserPrincipal principal) {
        // If already logged in, bounce to role page
        if (principal != null) {
            return "redirect:/student/feed";
        }
        return "login";
    }

    // Show signup page (alias /register too)
    @GetMapping({"/signup", "/register"})
    public String signupForm(Model model) {
        model.addAttribute("form", new SignUpForm());
        return "signup";
    }

    // Handle signup
    @PostMapping({"/signup", "/register"})
    public String doSignup(@Valid @ModelAttribute("form") SignUpForm form,
                           BindingResult binding,
                           Model model) {
        if (binding.hasErrors()) {
            return "signup";
        }
        try {
            signupService.registerStudent(form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "signup";
        }
        // redirect to login with a flag to show a “registered” message
        return "redirect:/login?registered";
    }

    // Render a logout page with a button that POSTS to /logout (required by Spring Security)
    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }
}