package com.pigeonpost.android.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    /*
     * Android Emulator:
     * 10.0.2.2 refers to the development computer.
     *
     * Do not use localhost here. Inside the emulator,
     * localhost refers to the emulator itself.
     */
    private static final String BASE_URL =
            "http://10.0.2.2:8080/";

    private static ApiService apiService;

    private RetrofitClient() {
        // Prevent construction.
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();

            loggingInterceptor.setLevel(
                    HttpLoggingInterceptor.Level.BODY
            );

            OkHttpClient okHttpClient =
                    new OkHttpClient.Builder()
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

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}