package com.wgu.d424.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String category;
    private String content;
    private String inputType; // "MANUAL" or "VOICE"
    private long createdAt;
    private long updatedAt;

    public Note(String category, String content, String inputType, long createdAt, long updatedAt) {
        this.category = category;
        this.content = content;
        this.inputType = inputType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getInputType() {
        return inputType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}