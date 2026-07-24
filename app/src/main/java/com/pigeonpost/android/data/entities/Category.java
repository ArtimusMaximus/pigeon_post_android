package com.pigeonpost.android.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey
    @NonNull
    private Integer id;

    private String name;
    private String color;

    public Category(
            @NonNull Integer id,
            String name,
            String color
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}