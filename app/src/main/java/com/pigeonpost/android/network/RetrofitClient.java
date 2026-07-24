package com.pigeonpost.android.network;

import android.content.Context;

import com.pigeonpost.android.security.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final String BASE_URL =
            "http://10.0.2.2:8080/";

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

        AuthInterceptor authInterceptor =
                new AuthInterceptor(tokenManager);

        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor();

        /*
         * BASIC logs the request method, URL and response status
         * without logging complete passwords, tokens or response bodies.
         */
        loggingInterceptor.setLevel(
                HttpLoggingInterceptor.Level.BASIC
        );

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(authInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        return retrofit.create(ApiService.class);
    }
}