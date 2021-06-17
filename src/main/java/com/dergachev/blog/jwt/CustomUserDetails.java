package com.dergachev.blog.jwt;

import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.entity.user.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private String email;
    private String password;
    private UserStatus userStatus;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public static CustomUserDetails fromUserToCustomUserDetails(User user) {
        CustomUserDetails c = new CustomUserDetails();
        c.email = user.getEmail();
        c.password = user.getPassword();
        c.userStatus = user.getStatus();
        c.grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRoleEntity().getName()));
        return c;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (userStatus.equals(UserStatus.ACTIVE)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
