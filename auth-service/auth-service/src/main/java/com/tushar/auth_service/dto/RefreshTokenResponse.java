package com.tushar.auth_service.dto;

public class RefreshTokenResponse {

    private String token;
    private String refreshToken;

    public RefreshTokenResponse() {
    }

    public RefreshTokenResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}