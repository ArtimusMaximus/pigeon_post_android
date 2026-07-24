package com.pigeonpost.android.network;

import com.pigeonpost.android.network.dto.AuthResponse;
import com.pigeonpost.android.network.dto.LoginRequest;

import com.pigeonpost.android.network.dto.NoteResponse;
import com.pigeonpost.android.network.dto.PagedResponse;
import com.pigeonpost.android.network.dto.CreateNoteRequest;
import com.pigeonpost.android.network.dto.UpdateNoteRequest;
import com.pigeonpost.android.network.dto.CategoryResponse;
import com.pigeonpost.android.network.dto.RefreshTokenRequest;
import com.pigeonpost.android.network.dto.CreateCategoryRequest;
import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/notes")
    Call<NoteResponse> createNote(
            @Body CreateNoteRequest request
    );

    @GET("api/notes")
    Call<PagedResponse<NoteResponse>> getNotes(
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("api/notes/{id}")
    Call<NoteResponse> updateNote(
            @Path("id") Long id,
            @Body UpdateNoteRequest request
    );
    @DELETE("api/notes/{id}")
    Call<Void> deleteNote(
            @Path("id") Long id
    );
    @GET("api/categories")
    Call<List<CategoryResponse>> getCategories();
    @POST("api/auth/login")
    Call<AuthResponse> login(
            @Body LoginRequest request
    );
    @POST("api/auth/refresh")
    Call<AuthResponse> refreshToken(
            @Body RefreshTokenRequest request
    );
    @POST("api/categories")
    Call<CategoryResponse> createCategory(
            @Body CreateCategoryRequest request
    );
}