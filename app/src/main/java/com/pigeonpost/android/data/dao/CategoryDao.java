package com.pigeonpost.android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pigeonpost.android.data.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<Category> categories);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category getCategoryById(Integer id);

    @Query("DELETE FROM categories")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Category category);

    @Query("DELETE FROM categories WHERE id = :categoryId")
    void deleteById(Integer categoryId);
}