package com.wgu.d424.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.wgu.d424.data.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes ORDER BY createdAt DESC LIMIT 5")
    List<Note> getTopFiveRecentNotes();

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY createdAt DESC")
    List<Note> getNotesByCategory(String category);

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    List<Note> searchNotesByKeyword(String keyword);

    @Query("SELECT * FROM notes WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    List<Note> getNotesByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteById(int id);
}