package com.pigeonpost.android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pigeonpost.android.data.entities.Note;
import com.pigeonpost.android.data.entities.CategoryCount;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes WHERE isPrivate = 0 ORDER BY createdAt DESC")
    List<Note> getAllPublicNotes();

    @Query("SELECT * FROM notes WHERE isPrivate = 1 ORDER BY createdAt DESC")
    List<Note> getAllPrivateNotes();

    @Query("SELECT * FROM notes WHERE isPrivate = 0 ORDER BY createdAt DESC LIMIT 5")
    List<Note> getTopFiveRecentPublicNotes();

    @Query("SELECT * FROM notes ORDER BY createdAt DESC LIMIT 5")
    List<Note> getTopFiveRecentNotes();

    @Query("SELECT * FROM notes WHERE category = :category AND isPrivate = 0 ORDER BY createdAt DESC")
    List<Note> getNotesByCategory(String category);

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :keyword || '%' AND isPrivate = 0 ORDER BY createdAt DESC")
    List<Note> searchNotesByKeyword(String keyword);

    @Query("SELECT * FROM notes WHERE createdAt BETWEEN :startDate AND :endDate AND isPrivate = 0 ORDER BY createdAt DESC")
    List<Note> getNotesByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteById(int id);

    @Query("SELECT * FROM notes " +
            "WHERE (:keyword = '' OR content LIKE '%' || :keyword || '%') " +
            "AND (:category = 'All' OR category = :category) " +
            "AND (:startDate = 0 OR createdAt >= :startDate) " +
            "AND (:endDate = 0 OR createdAt <= :endDate) " +
            "ORDER BY createdAt DESC")
    List<Note> searchNotes(String keyword, String category, long startDate, long endDate);

    @Query("SELECT COUNT(*) FROM notes")
    int getTotalNoteCount();

    @Query("SELECT category, COUNT(*) as noteCount FROM notes GROUP BY category ORDER BY noteCount DESC")
    List<CategoryCount> getNoteCountByCategory();

    @Query("SELECT MIN(createdAt) FROM notes")
    Long getOldestNoteDate();

    @Query("SELECT MAX(createdAt) FROM notes")
    Long getNewestNoteDate();

    @Query("SELECT category FROM notes GROUP BY category ORDER BY COUNT(*) DESC LIMIT 1")
    String getMostUsedCategory();

    @Query("SELECT AVG(LENGTH(content)) FROM notes")
    Double getAverageNoteLength();

}