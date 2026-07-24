package com.pigeonpost.android.network;

import com.pigeonpost.android.network.dto.AuthResponse;
import com.pigeonpost.android.network.dto.LoginRequest;

import com.pigeonpost.android.network.dto.NoteResponse;
import com.pigeonpost.android.network.dto.PagedResponse;
import com.pigeonpost.android.network.dto.CreateNoteRequest;

import retrofit2.http.GET;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> login(
            @Body LoginRequest request
    );

    @POST("api/notes")
    Call<NoteResponse> createNote(
            @Body CreateNoteRequest request
    );

    @GET("api/notes")
    Call<PagedResponse<NoteResponse>> getNotes(
            @Query("page") int page,
            @Query("size") int size
    );
}