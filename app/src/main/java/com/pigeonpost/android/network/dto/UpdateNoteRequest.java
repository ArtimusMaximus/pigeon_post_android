package com.pigeonpost.android.network.dto;

public class UpdateNoteRequest {

    private String title;
    private String content;
    private boolean privateNote;
    private Integer categoryId;

    public UpdateNoteRequest(
            String title,
            String content,
            boolean privateNote,
            Integer categoryId
    ) {
        this.title = title;
        this.content = content;
        this.privateNote = privateNote;
        this.categoryId = categoryId;
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

    public Integer getCategoryId() {
        return categoryId;
    }
}