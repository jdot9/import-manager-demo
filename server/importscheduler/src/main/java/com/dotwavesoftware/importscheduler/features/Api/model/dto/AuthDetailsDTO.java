package com.dotwavesoftware.importscheduler.features.Api.model.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class AuthDetailsDTO {
    private String type; // OAuth, API_KEY, BASIC
    private String token;
    private String refreshToken;
    private String apiKey;
    private String username;
    private String password; // optional, consider encryption
    private List<String> scopes;
    private Long expiresAt; // timestamp
    private Map<String, Object> additionalParameters; // for dynamic fields
}


