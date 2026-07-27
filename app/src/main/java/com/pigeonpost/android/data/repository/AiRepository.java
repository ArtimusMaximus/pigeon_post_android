package com.pigeonpost.android.data.repository;

import android.content.Context;

import com.pigeonpost.android.data.remote.dto.AiAnswerResponse;
import com.pigeonpost.android.data.remote.dto.AiQuestionRequest;
import com.pigeonpost.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiRepository {

    private final Context applicationContext;

    public AiRepository(Context context) {
        this.applicationContext =
                context.getApplicationContext();
    }

    public void askQuestion(
            String question,
            AiCallback callback
    ) {
        if (question == null || question.trim().isEmpty()) {
            callback.onError(
                    -1,
                    "Question cannot be empty."
            );
            return;
        }

        AiQuestionRequest request =
                new AiQuestionRequest(
                        question.trim()
                );

        RetrofitClient
                .getApiService(applicationContext)
                .askAi(request)
                .enqueue(new Callback<AiAnswerResponse>() {

                    @Override
                    public void onResponse(
                            Call<AiAnswerResponse> call,
                            Response<AiAnswerResponse> response
                    ) {
                        if (!response.isSuccessful()) {
                            String message;

                            if (response.code() == 401) {
                                message =
                                        "Your session has expired. Please log in again.";
                            } else {
                                message =
                                        "Server returned HTTP "
                                                + response.code();
                            }

                            callback.onError(
                                    response.code(),
                                    message
                            );
                            return;
                        }

                        AiAnswerResponse body =
                                response.body();

                        if (body == null) {
                            callback.onError(
                                    response.code(),
                                    "Server returned an empty response."
                            );
                            return;
                        }

                        if (body.getAnswer() == null
                                || body.getAnswer().trim().isEmpty()) {
                            callback.onError(
                                    response.code(),
                                    "The AI service returned an empty answer."
                            );
                            return;
                        }

                        callback.onSuccess(body);
                    }

                    @Override
                    public void onFailure(
                            Call<AiAnswerResponse> call,
                            Throwable throwable
                    ) {
                        String message =
                                throwable.getMessage() == null
                                        ? "Unable to reach the AI service."
                                        : throwable.getMessage();

                        callback.onError(
                                -1,
                                message
                        );
                    }
                });
    }

    public interface AiCallback {

        void onSuccess(
                AiAnswerResponse response
        );

        void onError(
                int statusCode,
                String message
        );
    }
}