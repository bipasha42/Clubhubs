package com.example.clubhub4.security;

import com.example.clubhub4.entity.User;
import com.example.clubhub4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring passes "username" here; we'll configure it to be "email" in formLogin.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Map DB role (e.g., STUDENT) to a Spring Security role (ROLE_STUDENT)
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // password is plain per your DB; NoOpPasswordEncoder will do raw equals
        return new AppUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(), // plain per your setup
                authorities
        );

    }
}