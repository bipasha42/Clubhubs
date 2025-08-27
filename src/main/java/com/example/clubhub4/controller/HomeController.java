package com.example.clubhub4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/dashboard"})
    public String home() {
        return "dashboard";
    }
}