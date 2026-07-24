package com.pigeonpost.android.network.dto;

public class UpdateCategoryRequest {

    private final String name;
    private final String color;

    public UpdateCategoryRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}