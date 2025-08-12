package com.example.clubhub4.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        boolean isStudent = hasRole(authentication, "ROLE_STUDENT");
        boolean isClubAdmin = hasRole(authentication, "ROLE_CLUB_ADMIN");
        boolean isUnivAdmin = hasRole(authentication, "ROLE_UNIVERSITY_ADMIN");

        if (isUnivAdmin) {
            response.sendRedirect(request.getContextPath() + "/university/clubs");
        } else if (isClubAdmin) {
            response.sendRedirect(request.getContextPath() + "/club/feed");
        } else if (isStudent) {
            response.sendRedirect(request.getContextPath() + "/student/feed");
        } else {
            response.sendRedirect(request.getContextPath() + "/403");
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (role.equals(ga.getAuthority())) return true;
        }
        return false;
    }
}