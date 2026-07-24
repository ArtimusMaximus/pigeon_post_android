package com.pigeonpost.android.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey
    private long id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private boolean privateNote;

    @NonNull
    private String createdAt;

    @NonNull
    private String updatedAt;

    @Nullable
    private Integer categoryId;

    @Nullable
    private String categoryName;

    @Nullable
    private String categoryColor;

    public Note(
            long id,
            @NonNull String title,
            @NonNull String content,
            boolean privateNote,
            @NonNull String createdAt,
            @NonNull String updatedAt,
            @Nullable Integer categoryId,
            @Nullable String categoryName,
            @Nullable String categoryColor
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.privateNote = privateNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public boolean isPrivateNote() {
        return privateNote;
    }

    public void setPrivateNote(boolean privateNote) {
        this.privateNote = privateNote;
    }

    @NonNull
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull String createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@NonNull String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@Nullable Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Nullable
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@Nullable String categoryName) {
        this.categoryName = categoryName;
    }

    @Nullable
    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(@Nullable String categoryColor) {
        this.categoryColor = categoryColor;
    }
}