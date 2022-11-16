package com.igorkucher.authmicroservice.security.jwt;

import com.igorkucher.authmicroservice.model.Role;
import com.igorkucher.authmicroservice.model.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    @Value("${authmicroservice.jwtCookieName}")
    private String jwtCookie;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer_";

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public String generateToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(getLoginFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }



    public Date getExpirationDate(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateToken(user.getUsername(), user.getRole());
        return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    }
}
