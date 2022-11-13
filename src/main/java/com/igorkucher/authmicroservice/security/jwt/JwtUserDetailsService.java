package com.igorkucher.authmicroservice.security.jwt;

import com.igorkucher.authmicroservice.model.User;
import com.igorkucher.authmicroservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userService.readByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + userName + " not found");
        }
        JwtUserDetails jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername - user with email {} successfully loaded", userName);
        return jwtUser;
    }
}
