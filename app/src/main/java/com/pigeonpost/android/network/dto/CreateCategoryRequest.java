package com.pigeonpost.android.network.dto;

public class CreateCategoryRequest {

    private final String name;
    private final String color;

    public CreateCategoryRequest(
            String name,
            String color
    ) {
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