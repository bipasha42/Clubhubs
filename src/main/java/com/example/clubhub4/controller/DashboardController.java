package com.example.clubhub4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/dashboard"})
    public String showDashboard() {
        return "dashboard";
    }
}
