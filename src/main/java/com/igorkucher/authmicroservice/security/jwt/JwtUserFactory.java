package com.igorkucher.authmicroservice.security.jwt;

import com.igorkucher.authmicroservice.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public final class JwtUserFactory {

    public JwtUserFactory() {}

    public static JwtUserDetails create(User user) {
        return new JwtUserDetails(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName())));
    }
}
