package com.pigeonpost.android.data.remote.dto;

public class AiQuestionRequest {

    private final String question;

    public AiQuestionRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}