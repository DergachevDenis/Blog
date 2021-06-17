package com.dergachev.blog.jwt;

import com.dergachev.blog.entity.user.User;
import com.dergachev.blog.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceImpl userService;

    @Autowired
    public CustomUserDetailsService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if(user == null){
            log.error("IN loadUserByUsername - user with email {} not found", email);
            throw new UsernameNotFoundException(String.format("User with email %s not found", email));
        }
        return CustomUserDetails.fromUserToCustomUserDetails(user);
    }
}
