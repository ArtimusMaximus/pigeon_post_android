package com.pigeonpost.android.data.repository;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.util.Log;

import com.pigeonpost.android.data.dao.NoteDao;
import com.pigeonpost.android.data.db.AppDatabase;
import com.pigeonpost.android.data.entities.Note;

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

import com.pigeonpost.android.data.mapper.NoteMapper;
import com.pigeonpost.android.security.TokenManager;

import com.pigeonpost.android.network.dto.CreateNoteRequest;
import com.pigeonpost.android.network.dto.NoteResponse;
import com.pigeonpost.android.data.mapper.NoteMapper;

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
                String safeKeyword =
                        keyword == null ? "" : keyword.trim();

                String safeCategory =
                        category == null ? "All" : category;

                String startTimestamp =
                        startDate > 0
                                ? Instant.ofEpochMilli(startDate).toString()
                                : Instant.EPOCH.toString();

                String endTimestamp =
                        endDate > 0
                                ? Instant.ofEpochMilli(endDate).toString()
                                : Instant.parse(
                                "9999-12-31T23:59:59.999Z"
                        ).toString();
                //temp
                List<Note> allNotes = noteDao.getAllNotes();

                for (Note note : allNotes) {
                    Log.d(
                            "PIGEON_ALL_ROOM_NOTES",
                            "id=" + note.getId()
                                    + ", createdAt=" + note.getCreatedAt()
                                    + ", category=" + note.getCategoryName()
                                    + ", content=" + note.getContent()
                    );
                }
                //temp


                List<Note> notes = noteDao.searchNotes(
                        safeKeyword,
                        safeCategory,
                        startTimestamp,
                        endTimestamp
                );

                for (Note note : notes) {
                    Log.d(
                            "PIGEON_SEARCH_ORDER",
                            "id=" + note.getId()
                                    + ", createdAt=" + note.getCreatedAt()
                                    + ", content=" + note.getContent()
                    );
                }

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
//                List<NoteResponse> notes,
//                long totalElements
                PagedResponse<NoteResponse> response
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

//                        callback.onSuccess(
//                                notes,
//                                body.getTotalElements()
//                        );
                        callback.onSuccess(
                                body
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
            SyncNotesCallback callback
    ) {
        syncServerNotesPage(
                0,
                20,
                new ArrayList<>(),
                callback
        );
    }


    private void syncServerNotesPage(
            int page,
            int size,
            List<Note> cachedNotes,
            SyncNotesCallback callback
    ) {

        fetchServerNotes(
                page,
                size,
                new ServerNotesCallback() {

                    @Override
                    public void onSuccess(
                            PagedResponse<NoteResponse> response
                    ) {

                        for (NoteResponse noteResponse :
                                response.getContent()) {

                            cachedNotes.add(
                                    NoteMapper.fromResponse(noteResponse)
                            );
                        }

                        if (!response.isLast()) {

                            syncServerNotesPage(
                                    page + 1,
                                    size,
                                    cachedNotes,
                                    callback
                            );

                            return;
                        }

                        executorService.execute(() -> {

                            try {

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

    public interface SyncCallback {
        void onSuccess(int noteCount);
        void onError(Exception exception);
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

    public void createNote(
            Integer categoryId,
            String title,
            String content,
            boolean privateNote,
            CreateNoteCallback callback
    ) {
        CreateNoteRequest request = new CreateNoteRequest(
                categoryId,
                title,
                content,
                privateNote
        );

        RetrofitClient.getApiService(applicationContext)
                .createNote(request)
                .enqueue(new Callback<NoteResponse>() {
                    @Override
                    public void onResponse(
                            Call<NoteResponse> call,
                            Response<NoteResponse> response
                    ) {
                        if (!response.isSuccessful()) {
                            callback.onError(
                                    response.code(),
                                    "Server returned HTTP " + response.code()
                            );
                            return;
                        }

                        NoteResponse body = response.body();

                        if (body == null) {
                            callback.onError(
                                    response.code(),
                                    "Server returned an empty response."
                            );
                            return;
                        }

                        Note cachedNote = NoteMapper.fromResponse(body);

                        executorService.execute(() -> {
                            try {
                                noteDao.upsert(cachedNote);
                                callback.onSuccess(cachedNote);
                            } catch (Exception exception) {
                                callback.onError(
                                        -1,
                                        exception.getMessage() == null
                                                ? "Note was created but could not be cached."
                                                : exception.getMessage()
                                );
                            }
                        });
                    }

                    @Override
                    public void onFailure(
                            Call<NoteResponse> call,
                            Throwable throwable
                    ) {
                        callback.onError(
                                -1,
                                throwable.getMessage() == null
                                        ? "Unable to reach the server."
                                        : throwable.getMessage()
                        );
                    }
                });
    }

    public interface CreateNoteCallback {

        void onSuccess(Note note);

        void onError(int statusCode, String message);
    }

    public void close() {
        executorService.shutdown();
    }
}