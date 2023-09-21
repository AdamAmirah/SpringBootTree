package com.treeTest.treeTest.auth;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            logger.info("User authenticated: {}", request.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new AuthenticationResponse("Authentication successful");

        } catch (BadCredentialsException ex) {
            logger.error("Authentication failed for user '{}': {}", request.getUsername(), ex.getMessage());
            throw new Error("Invalid credentials");
        }
        catch (Exception ex) {
            logger.error("An error occurred during authentication for user '{}': {}", request.getUsername(), ex.getMessage());
            throw new Error("An error occurred during authentication");
        }
    }
}
