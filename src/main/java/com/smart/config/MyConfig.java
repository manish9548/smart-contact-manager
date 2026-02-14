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

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                // ✅ allow static resources explicitly
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                // ✅ allow public pages
                .requestMatchers("/", "/about", "/signup", "/do_register", "/login").permitAll()

                // ✅ role based access
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form

            	    .loginPage("/signin")            // fixed typo
            	    .loginProcessingUrl("/dologin") // must match form action
            	    .defaultSuccessUrl("/user/index", true)

            	    .permitAll()
            	)


            .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
