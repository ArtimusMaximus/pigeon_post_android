package com.pigeonpost.android.data.repository;

import android.content.Context;

import com.pigeonpost.android.data.dao.NoteDao;
import com.pigeonpost.android.data.db.AppDatabase;
import com.pigeonpost.android.data.entities.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    public interface NotesCallback {
        void onSuccess(List<Note> notes);
        void onError(Exception exception);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(Exception exception);
    }

    private final NoteDao noteDao;
    private final ExecutorService executorService;

    public NoteRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(
                context.getApplicationContext()
        );

        noteDao = database.noteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void searchNotes(
            String keyword,
            String category,
            long startDate,
            long endDate,
            NotesCallback callback
    ) {
        executorService.execute(() -> {
            try {
                List<Note> notes = noteDao.searchNotes(
                        keyword,
                        category,
                        startDate,
                        endDate
                );

                callback.onSuccess(notes);
            } catch (Exception exception) {
                callback.onError(exception);
            }
        });
    }

    public void updateNote(
            Note note,
            OperationCallback callback
    ) {
        executorService.execute(() -> {
            try {
                noteDao.update(note);
                callback.onSuccess();
            } catch (Exception exception) {
                callback.onError(exception);
            }
        });
    }

    public void deleteNote(
            Note note,
            OperationCallback callback
    ) {
        executorService.execute(() -> {
            try {
                noteDao.delete(note);
                callback.onSuccess();
            } catch (Exception exception) {
                callback.onError(exception);
            }
        });
    }

    public void close() {
        executorService.shutdown();
    }
}