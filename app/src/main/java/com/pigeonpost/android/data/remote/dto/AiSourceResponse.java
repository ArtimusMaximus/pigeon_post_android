package com.pigeonpost.android.data.remote.dto;

public class AiSourceResponse {

    private Long noteId;
    private String title;
    private String content;
    private String categoryName;
    private String categoryColor;
    private String createdAt;
    private Double distance;

    public Long getNoteId() {
        return noteId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Double getDistance() {
        return distance;
    }
}