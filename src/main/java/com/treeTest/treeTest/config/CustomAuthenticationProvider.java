package com.treeTest.treeTest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
            if (username.equals("admin") && password.equals("admin")) {
                return new UsernamePasswordAuthenticationToken(username, password, Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else if (username.equals("user") && password.equals("user")) {
                return new UsernamePasswordAuthenticationToken(username, password, Set.of(new SimpleGrantedAuthority("ROLE_USER")));
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", username, e);
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
