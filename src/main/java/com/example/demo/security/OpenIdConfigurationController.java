package com.example.demo.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenIdConfigurationController {

    private final String issuer;
    private final String tokenEndpoint;

    public OpenIdConfigurationController(
            @Value("${security.issuer:http://localhost:8080}") String issuer,
            @Value("${security.oauth2.token-endpoint:/token}") String tokenEndpoint) {
        this.issuer = issuer;
        this.tokenEndpoint = tokenEndpoint;
    }

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> configuration() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("issuer", issuer);
        config.put("authorization_endpoint", issuer + "/oauth/authorize");
        config.put("token_endpoint", issuer + tokenEndpoint);
        config.put("userinfo_endpoint", issuer + "/userinfo");
        config.put("jwks_uri", issuer + "/.well-known/jwks.json");
        config.put("response_types_supported", new String[]{"code", "token", "id_token"});
        config.put("subject_types_supported", new String[]{"public"});
        config.put("id_token_signing_alg_values_supported", new String[]{"RS256", "HS256"});
        config.put("scopes_supported", new String[]{"openid", "profile", "email"});
        config.put("token_endpoint_auth_methods_supported", new String[]{"client_secret_basic", "client_secret_post", "none"});
        return config;
    }
}
