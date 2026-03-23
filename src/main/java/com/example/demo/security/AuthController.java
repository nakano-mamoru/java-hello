package com.example.demo.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("GET /api/auth/me called by {}", userDetails == null ? "anonymous" : userDetails.getUsername());
        if (userDetails == null) {
            return Map.of("authenticated", false);
        }
        return Map.of(
            "authenticated", true,
            "username", userDetails.getUsername(),
            "authorities", userDetails.getAuthorities()
        );
    }
}
