package com.example.clubhub4.config;

import com.example.clubhub4.security.RoleBasedAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RoleBasedAuthSuccessHandler successHandler;
    private final UserDetailsService userDetailsService1;

    // You asked for no encoding: this compares raw strings. Do NOT use in prod.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService1);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/dashboard", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/signup", "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/signup", "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/logout").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/explore/**", "/clubs/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/posts/*/like", "/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/me/posts/new").authenticated()
                        .requestMatchers(HttpMethod.POST, "/me/posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/clubs/*/follow").authenticated()
                        .requestMatchers(HttpMethod.POST, "/clubs/*/posts").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/clubs/*/posts/*/edit").authenticated()
                        .requestMatchers(HttpMethod.POST, "/clubs/*/posts/*/edit").authenticated()
                        .requestMatchers(HttpMethod.POST, "/clubs/*/posts/*/delete").authenticated()
                        .requestMatchers(HttpMethod.POST, "/posts/*/comments/*/delete").authenticated()

                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/club/**").hasRole("CLUB_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/club/posts/**").hasRole("CLUB_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/club/posts/**").hasRole("CLUB_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/club/posts/**").hasRole("CLUB_ADMIN")
                        .requestMatchers("/university/**").hasRole("UNIVERSITY_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")      // use email field
                        .passwordParameter("password")   // default is "password" anyway
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                )
                .httpBasic(Customizer.withDefaults()); // optional for testing APIs

        return http.build();
    }
}