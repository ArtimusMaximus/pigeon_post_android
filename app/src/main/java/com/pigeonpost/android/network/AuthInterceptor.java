package com.pigeonpost.android.network;

import androidx.annotation.NonNull;

import com.pigeonpost.android.security.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String authorizationHeader =
                tokenManager.getAuthorizationHeader();

        if (authorizationHeader == null) {
            return chain.proceed(originalRequest);
        }

        Request authenticatedRequest = originalRequest
                .newBuilder()
                .header("Authorization", authorizationHeader)
                .build();

        return chain.proceed(authenticatedRequest);
    }
}