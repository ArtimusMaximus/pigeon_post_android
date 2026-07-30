package com.pigeonpost.android.network;

import android.content.Context;

import com.pigeonpost.android.security.SessionManager;
import com.pigeonpost.android.security.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

//    private static final String BASE_URL =  "http://10.0.2.2:8080/"; // DEV
    private static final String BASE_URL = "https://pigeonpost.es9.app/"; // PROD
    private static volatile ApiService apiService;


    private RetrofitClient() {
        // Prevent construction.
    }

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    apiService = createApiService(
                            context.getApplicationContext()
                    );
                }
            }
        }

        return apiService;
    }

    private static ApiService createApiService(Context context) {
        TokenManager tokenManager = new TokenManager(context);

        SessionManager sessionManager = new SessionManager(context);

        HttpLoggingInterceptor loggingInterceptor =
                createLoggingInterceptor();

        /*
         * This separate service performs login and token refresh
         * without attaching TokenAuthenticator.
         */
        OkHttpClient refreshHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build();

        ApiService refreshApiService =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(refreshHttpClient)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build()
                        .create(ApiService.class);

        AuthInterceptor authInterceptor =
                new AuthInterceptor(tokenManager);


        TokenAuthenticator tokenAuthenticator =
                new TokenAuthenticator(
                        tokenManager,
                        refreshApiService,
                        sessionManager
                );

        OkHttpClient authenticatedHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(authInterceptor)
                        .authenticator(tokenAuthenticator)
                        .addInterceptor(loggingInterceptor)
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(authenticatedHttpClient)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        return retrofit.create(ApiService.class);
    }

    private static HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(
                HttpLoggingInterceptor.Level.BASIC
        );

        return loggingInterceptor;
    }
}