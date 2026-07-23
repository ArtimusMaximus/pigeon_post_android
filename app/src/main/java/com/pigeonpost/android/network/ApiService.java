package com.pigeonpost.android.network;

import com.pigeonpost.android.network.dto.AuthResponse;
import com.pigeonpost.android.network.dto.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> login(
            @Body LoginRequest request
    );
}