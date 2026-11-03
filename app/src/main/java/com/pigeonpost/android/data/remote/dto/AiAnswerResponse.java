package com.pigeonpost.android.data.remote.dto;

import java.util.List;

public class AiAnswerResponse {

    private String model;
    private String answer;
    private List<AiSourceResponse> sources;
    private List<AiSourceResponse> relatedSources;

    public List<AiSourceResponse> getRelatedSources() { return relatedSources; }

    public String getModel() {
        return model;
    }

    public String getAnswer() {
        return answer;
    }

    public List<AiSourceResponse> getSources() {
        return sources;
    }
}