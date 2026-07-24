package com.pigeonpost.android.network.dto;

public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private boolean privateNote;
    private String createdAt;
    private String updatedAt;
    private CategoryResponse category;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isPrivateNote() {
        return privateNote;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public CategoryResponse getCategory() {
        return category;
    }
}