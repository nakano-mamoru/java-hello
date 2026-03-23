package com.example.demo.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public TokenController(AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping({"/token", "/oauth/token"})
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        String grantType = request.getGrant_type();
        if (grantType == null || grantType.isBlank()) {
            grantType = "password";
        }

        try {
            if ("password".equals(grantType)) {
                if (request.getUsername() == null || request.getPassword() == null) {
                    return ResponseEntity.badRequest().build();
                }
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );
                List<String> roles = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                String accessToken = jwtTokenUtil.generateAccessToken(request.getUsername(), roles);
                String refreshToken = jwtTokenUtil.isRefreshTokenEnabled() ? jwtTokenUtil.generateRefreshToken(request.getUsername(), roles) : null;
                return ResponseEntity.ok(new TokenResponse(accessToken, "Bearer", jwtTokenUtil.getAccessTokenExpirationSeconds(), refreshToken));
            }

            if ("refresh_token".equals(grantType)) {
                if (!jwtTokenUtil.isRefreshTokenEnabled() || request.getRefresh_token() == null) {
                    return ResponseEntity.status(400).build();
                }

                String refreshToken = request.getRefresh_token();
                if (!jwtTokenUtil.isTokenValid(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
                    return ResponseEntity.status(401).build();
                }

                String username = jwtTokenUtil.getUsername(refreshToken);
                List<String> roles = jwtTokenUtil.getRoles(refreshToken);
                String accessToken = jwtTokenUtil.generateAccessToken(username, roles);
                String newRefreshToken = jwtTokenUtil.isRefreshTokenEnabled() ? jwtTokenUtil.generateRefreshToken(username, roles) : null;
                return ResponseEntity.ok(new TokenResponse(accessToken, "Bearer", jwtTokenUtil.getAccessTokenExpirationSeconds(), newRefreshToken));
            }

            return ResponseEntity.badRequest().build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
