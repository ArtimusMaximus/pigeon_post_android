package com.pigeonpost.android.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pigeonpost.android.network.dto.AuthResponse;
import com.pigeonpost.android.network.dto.RefreshTokenRequest;
import com.pigeonpost.android.security.SessionManager;
import com.pigeonpost.android.security.TokenManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private final TokenManager tokenManager;
    private final ApiService refreshApiService;
    private final SessionManager sessionManager;

    public TokenAuthenticator(
            TokenManager tokenManager,
            ApiService refreshApiService,
            SessionManager sessionManager
    ) {
        this.tokenManager = tokenManager;
        this.refreshApiService = refreshApiService;
        this.sessionManager = sessionManager;
    }

    @Nullable
    @Override
    public Request authenticate(
            @Nullable Route route,
            @NonNull Response response
    ) throws IOException {

        /*
         * Prevent an infinite loop when the retried request
         * also returns 401.
         */
        if (responseCount(response) >= 2) {
            sessionManager.expireSession();
            return null;
        }

        String refreshToken = tokenManager.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            sessionManager.expireSession();
            return null;
        }

        synchronized (this) {
            /*
             * Another request may have refreshed the token while
             * this request was waiting for the synchronized block.
             */
            String failedAuthorizationHeader =
                    response.request().header("Authorization");

            String currentAuthorizationHeader =
                    tokenManager.getAuthorizationHeader();

            if (currentAuthorizationHeader != null
                    && failedAuthorizationHeader != null
                    && !currentAuthorizationHeader.equals(
                    failedAuthorizationHeader
            )) {

                return response.request()
                        .newBuilder()
                        .header(
                                "Authorization",
                                currentAuthorizationHeader
                        )
                        .build();
            }

            retrofit2.Response<AuthResponse> refreshResponse =
                    refreshApiService.refreshToken(
                            new RefreshTokenRequest(refreshToken)
                    ).execute();

            if (!refreshResponse.isSuccessful()
                    || refreshResponse.body() == null) {
                Log.e(
                        "PIGEON_AUTH",
                        "Token refresh failed. HTTP "
                                + refreshResponse.code()
                );
                sessionManager.expireSession();
                return null;
            }

            AuthResponse authResponse = refreshResponse.body();

            if (authResponse.getAccessToken() == null
                    || authResponse.getRefreshToken() == null) {

                sessionManager.expireSession();
                return null;
            }

            tokenManager.saveTokens(
                    authResponse.getAccessToken(),
                    authResponse.getRefreshToken(),
                    authResponse.getTokenType()
            );

            String newAuthorizationHeader =
                    tokenManager.getAuthorizationHeader();

            if (newAuthorizationHeader == null) {
                sessionManager.expireSession();
                return null;
            }

            return response.request()
                    .newBuilder()
                    .header(
                            "Authorization",
                            newAuthorizationHeader
                    )
                    .build();
        }
    }

    private int responseCount(Response response) {
        int count = 1;
        Response priorResponse = response.priorResponse();

        while (priorResponse != null) {
            count++;
            priorResponse = priorResponse.priorResponse();
        }

        return count;
    }
}