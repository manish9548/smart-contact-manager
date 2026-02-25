package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MyConfig {

    // ✅ Custom UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    // ✅ Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ✅ Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                // Static resources
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                // Public pages
                .requestMatchers(
                        "/", 
                        "/about", 
                        "/signup", 
                        "/do_register",
                        "/signin"
                ).permitAll()

                // Forgot Password URLs
                .requestMatchers(
                        "/forgot-password",
                        "/send-otp",
                        "/verify-otp",
                        "/reset-password",
                        "/update-password"
                ).permitAll()

                // Role based access
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")

                // Any other request
                .anyRequest().authenticated()
            )

            // Login configuration
            .formLogin(form -> form
                    .loginPage("/login")   // 👈 yaha change
                    .loginProcessingUrl("/dologin")
                    .defaultSuccessUrl("/user/index", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )

            // Logout configuration
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/signin?logout=true")
                    .permitAll()
            )

            // CSRF (disable for development)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}