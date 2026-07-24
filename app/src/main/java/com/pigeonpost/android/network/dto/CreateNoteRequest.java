package com.pigeonpost.android.network.dto;

public class CreateNoteRequest {

    private final Integer categoryId;
    private final String title;
    private final String content;
    private final boolean privateNote;

    public CreateNoteRequest(
            Integer categoryId,
            String title,
            String content,
            boolean privateNote
    ) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.privateNote = privateNote;
    }

    public Integer getCategoryId() {
        return categoryId;
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
}