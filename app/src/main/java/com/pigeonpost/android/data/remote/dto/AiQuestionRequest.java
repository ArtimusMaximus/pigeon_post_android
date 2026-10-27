package com.pigeonpost.android.data.remote.dto;

import java.util.List;
import java.util.ArrayList;

public class AiQuestionRequest {

    private final String question;
    private final String answerMode;
    private final List<Long> selectedSourceIds;

    public AiQuestionRequest(String question) {
        this(question, "STANDARD", java.util.Collections.emptyList());
    }

    public AiQuestionRequest(String question, String answerMode, List<Long> selectedSourceIds) {
        this.question = question;
        this.answerMode = answerMode;
        this.selectedSourceIds = new ArrayList<>(selectedSourceIds);
    }

    public String getQuestion() {
        return question;
    }
}