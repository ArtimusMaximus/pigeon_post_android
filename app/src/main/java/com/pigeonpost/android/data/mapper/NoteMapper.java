package com.pigeonpost.android.data.mapper;

import com.pigeonpost.android.data.entities.Note;
import com.pigeonpost.android.network.dto.CategoryResponse;
import com.pigeonpost.android.network.dto.NoteResponse;

import java.time.OffsetDateTime;

public final class NoteMapper {

    private NoteMapper() {
        // Utility class.
    }

    public static Note fromResponse(NoteResponse response) {
        CategoryResponse category = response.getCategory();

        Integer categoryId = null;
        String categoryName = null;
        String categoryColor = null;

        if (category != null) {
            categoryId = category.getId();
            categoryName = category.getName();
            categoryColor = category.getColor();
        }

        String createdAt = OffsetDateTime
                .parse(response.getCreatedAt())
                .toInstant()
                .toString();

        String updatedAt = OffsetDateTime
                .parse(response.getUpdatedAt())
                .toInstant()
                .toString();

        return new Note(
                response.getId(),
                response.getTitle(),
                response.getContent(),
                response.isPrivateNote(),
                createdAt,
                updatedAt,
                categoryId,
                categoryName,
                categoryColor
        );
    }
}