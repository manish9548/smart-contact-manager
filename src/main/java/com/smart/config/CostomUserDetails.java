package com.smart.config;

import java.util.Collection;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.User;

public class CostomUserDetails implements UserDetails {

    private User user;

    public CostomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Agar role nahi hai to empty list return karo
    	SimpleGrantedAuthority	simpleGrantedAuthority=new SimpleGrantedAuthority(user.getRole());
        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;   // account expired nahi hai
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;   // account locked nahi hai
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;   // password expired nahi hai
    }

    @Override
    public boolean isEnabled() {
        return true;   // account enabled hai
    }
}
