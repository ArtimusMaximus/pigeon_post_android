package com.pigeonpost.android.data.repository;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;

import com.pigeonpost.android.data.dao.NoteDao;
import com.pigeonpost.android.data.db.AppDatabase;
import com.pigeonpost.android.data.entities.Note;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pigeonpost.android.network.RetrofitClient;
import com.pigeonpost.android.network.dto.NoteResponse;
import com.pigeonpost.android.network.dto.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pigeonpost.android.data.entities.Note;
import com.pigeonpost.android.data.mapper.NoteMapper;
import com.pigeonpost.android.security.TokenManager;
import com.pigeonpost.android.ui.LoginActivity;
import com.pigeonpost.android.ui.MainActivity;

import java.util.ArrayList;

public class NoteRepository {

    public interface NotesCallback {
        void onSuccess(List<Note> notes);
        void onError(Exception exception);
    }

    public interface ClearCacheCallback {

        void onComplete();

        void onError(String message);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(Exception exception);
    }
    //
    TokenManager tokenManager;
    NoteRepository noteRepository;
    //

    private final NoteDao noteDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Context applicationContext;

    public NoteRepository(Context context) {
        applicationContext = context.getApplicationContext();

        AppDatabase database = AppDatabase.getDatabase(applicationContext);

        noteDao = database.noteDao();
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
                String startTimeStamp = Instant.ofEpochMilli(startDate).toString();
                String endTimeStamp = Instant.ofEpochMilli(endDate).toString();

                List<Note> notes = noteDao.searchNotes(
                        keyword,
                        category,
                        startTimeStamp,
                        endTimeStamp
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

    public interface ServerNotesCallback {

        void onSuccess(
                List<NoteResponse> notes,
                long totalElements
        );

        void onError(
                int statusCode,
                String message
        );
    }

    public void fetchServerNotes(
            int page,
            int size,
            ServerNotesCallback callback
    ) {
        RetrofitClient.getApiService(applicationContext)
                .getNotes(page, size)
                .enqueue(new Callback<PagedResponse<NoteResponse>>() {
                    @Override
                    public void onResponse(
                            Call<PagedResponse<NoteResponse>> call,
                            Response<PagedResponse<NoteResponse>> response
                    ) {
                        if (!response.isSuccessful()) {
                            callback.onError(
                                    response.code(),
                                    "Server returned HTTP "
                                            + response.code()
                            );

                            return;
                        }

                        PagedResponse<NoteResponse> body =
                                response.body();

                        if (body == null) {
                            callback.onError(
                                    response.code(),
                                    "Server returned an empty response."
                            );

                            return;
                        }

                        List<NoteResponse> notes =
                                body.getContent();

                        if (notes == null) {
                            callback.onError(
                                    response.code(),
                                    "Server response did not contain notes."
                            );

                            return;
                        }

                        callback.onSuccess(
                                notes,
                                body.getTotalElements()
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<PagedResponse<NoteResponse>> call,
                            Throwable throwable
                    ) {
                        String message =
                                throwable.getMessage() == null
                                        ? "Unable to reach the server."
                                        : throwable.getMessage();

                        callback.onError(
                                -1,
                                message
                        );
                    }
                });
    }
    public interface SyncNotesCallback {

        void onSuccess(int cachedNoteCount);

        void onError(int statusCode, String message);
    }
    public interface LocalNotesCallback {

        void onSuccess(List<Note> notes);

        void onError(String message);
    }
    public void getLocalNotes(LocalNotesCallback callback) {
        executorService.execute(() -> {
            try {
                List<Note> notes =
                        noteDao.getAllNotes();

                callback.onSuccess(notes);
            } catch (Exception exception) {
                callback.onError(
                        exception.getMessage() == null
                                ? "Unable to load cached notes."
                                : exception.getMessage()
                );
            }
        });
    }
    public void syncServerNotes(
            int page,
            int size,
            SyncNotesCallback callback
    ) {
        fetchServerNotes(
                page,
                size,
                new ServerNotesCallback() {
                    @Override
                    public void onSuccess(
                            List<NoteResponse> responses,
                            long totalElements
                    ) {
                        executorService.execute(() -> {
                            try {
                                List<Note> cachedNotes =
                                        new ArrayList<>();

                                for (NoteResponse response : responses) {
                                    cachedNotes.add(
                                            NoteMapper.fromResponse(response)
                                    );
                                }

                                /*
                                 * Because this request loads the complete first
                                 * page and currently contains every server note,
                                 * replace the existing cache.
                                 */
                                noteDao.deleteAll();
                                noteDao.upsertAll(cachedNotes);

                                callback.onSuccess(
                                        cachedNotes.size()
                                );
                            } catch (Exception exception) {
                                callback.onError(
                                        -1,
                                        exception.getMessage() == null
                                                ? "Unable to update local cache."
                                                : exception.getMessage()
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(
                            int statusCode,
                            String message
                    ) {
                        callback.onError(
                                statusCode,
                                message
                        );
                    }
                }
        );
    }

    public void clearLocalCache(
            ClearCacheCallback callback
    ) {
        executorService.execute(() -> {
            try {
                noteDao.deleteAll();
                callback.onComplete();
            } catch (Exception exception) {
                callback.onError(
                        exception.getMessage() == null
                                ? "Unable to clear local cache."
                                : exception.getMessage()
                );
            }
        });
    }

    public void close() {
        executorService.shutdown();
    }
}