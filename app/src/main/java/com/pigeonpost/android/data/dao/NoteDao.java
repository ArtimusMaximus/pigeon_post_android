package com.pigeonpost.android.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pigeonpost.android.data.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<Note> notes);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes")
    void deleteAll();

    @Query(
            "SELECT * FROM notes " +
                    "ORDER BY createdAt DESC"
    )
    List<Note> getAllNotes();

    @Query(
            "SELECT * FROM notes " +
                    "ORDER BY createdAt DESC " +
                    "LIMIT :limit"
    )
    List<Note> getRecentNotes(int limit);

    @Query(
            "SELECT * FROM notes " +
                    "WHERE id = :noteId " +
                    "LIMIT 1"
    )
    Note getNoteById(long noteId);

    @Query(
            "SELECT * FROM notes " +
                    "WHERE (" +
                    "    :keyword = '' " +
                    "    OR title LIKE '%' || :keyword || '%' " +
                    "    OR content LIKE '%' || :keyword || '%' " +
                    ") " +
                    "AND (" +
                    "    :category = '' " +
                    "    OR :category = 'All' " +
                    "    OR (" +
                    "        :category = 'Other' " +
                    "        AND categoryId IS NULL" +
                    "    ) " +
                    "    OR categoryName = :category" +
                    ") " +
                    "AND createdAt >= :startDate " +
                    "AND createdAt <= :endDate " +
                    "ORDER BY createdAt DESC"
    )
    List<Note> searchNotes(
            String keyword,
            String category,
            String startDate,
            String endDate
    );


}