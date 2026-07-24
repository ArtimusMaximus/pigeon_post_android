package com.pigeonpost.android.security;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREFERENCES_NAME = "pigeonpost_auth";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String TOKEN_TYPE_KEY = "token_type";

    private final SharedPreferences preferences;

    public TokenManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(
                        PREFERENCES_NAME,
                        Context.MODE_PRIVATE
                );
    }

    public void saveTokens(
            String accessToken,
            String refreshToken,
            String tokenType
    ) {
        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessToken)
                .putString(REFRESH_TOKEN_KEY, refreshToken)
                .putString(TOKEN_TYPE_KEY, tokenType)
                .apply();
    }

    public String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN_KEY, null);
    }

    public String getTokenType() {
        return preferences.getString(TOKEN_TYPE_KEY, "Bearer");
    }

    public boolean hasAccessToken() {
        String accessToken = getAccessToken();

        return accessToken != null && !accessToken.isBlank();
    }

    public String getAuthorizationHeader() {
        String accessToken = getAccessToken();

        if (accessToken == null || accessToken.isBlank()) {
            return null;
        }

        return getTokenType() + " " + accessToken;
    }

    public void clearTokens() {
        preferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(TOKEN_TYPE_KEY)
                .apply();
    }

    public void clearRefreshTokenForTesting() { // testing for redirect to LoginActivity on expired refresh token
        preferences.edit()
                .remove(REFRESH_TOKEN_KEY)
                .apply();
    }
}