package com.pigeonpost.android.network.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}