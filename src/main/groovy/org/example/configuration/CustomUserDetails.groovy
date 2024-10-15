package org.example.configuration

import org.example.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails implements UserDetails{

    private final User user

    CustomUserDetails(User user) {
        this.user = user
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        user.roles.collect { new SimpleGrantedAuthority(it) }
    }

    @Override
    String getPassword() {
        user.password
    }

    @Override
    String getUsername() {
        user.username
    }

    @Override
    boolean isAccountNonExpired() {
        true
    }

    @Override
    boolean isAccountNonLocked() {
        true
    }

    @Override
    boolean isCredentialsNonExpired() {
        true
    }

    @Override
    boolean isEnabled() {
        true
    }
}
