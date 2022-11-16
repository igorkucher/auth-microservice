package com.igorkucher.authmicroservice.controller;

import com.igorkucher.authmicroservice.dto.request.LoginRequest;
import com.igorkucher.authmicroservice.dto.response.UserResponse;
import com.igorkucher.authmicroservice.model.User;
import com.igorkucher.authmicroservice.security.jwt.JwtProvider;
import com.igorkucher.authmicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
        User user = userService.readByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        String token = jwtProvider.generateToken(username, user.getRole());
        ResponseCookie jwtCookie = jwtProvider.generateJwtCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserResponse(user.getId(), user.getEmail(),
                        user.getFirstName(), user.getLastName(), user.getRole()));
    }
}
