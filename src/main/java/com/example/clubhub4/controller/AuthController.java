package com.example.clubhub4.controller;

import com.example.clubhub4.dto.SignUpForm;
import com.example.clubhub4.repository.UniversityRepository;
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
    private final UniversityRepository universityRepository;

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal AppUserPrincipal principal) {
        if (principal != null) return "redirect:/student/feed";
        return "login";
    }

    @GetMapping({"/signup", "/register"})
    public String signupForm(Model model) {
        model.addAttribute("form", new SignUpForm());
        model.addAttribute("universities", universityRepository.findAllByOrderByNameAsc());
        return "signup";
    }

    @PostMapping({"/signup", "/register"})
    public String doSignup(@Valid @ModelAttribute("form") SignUpForm form,
                           BindingResult binding,
                           Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("universities", universityRepository.findAllByOrderByNameAsc());
            return "signup";
        }
        try {
            signupService.registerStudent(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("universities", universityRepository.findAllByOrderByNameAsc());
            return "signup";
        }
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }
}