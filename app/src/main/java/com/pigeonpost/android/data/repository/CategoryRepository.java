package com.pigeonpost.android.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.pigeonpost.android.data.dao.CategoryDao;
import com.pigeonpost.android.data.db.AppDatabase;
import com.pigeonpost.android.data.entities.Category;
import com.pigeonpost.android.network.RetrofitClient;
import com.pigeonpost.android.network.dto.CategoryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private final Context applicationContext;
    private final CategoryDao categoryDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public CategoryRepository(Context context) {
        applicationContext = context.getApplicationContext();

        AppDatabase database =
                AppDatabase.getDatabase(applicationContext);

        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /*
     * Fetches categories from Spring, then replaces the local
     * Room category cache with the server results.
     */
    public void synchronizeCategories(
            CategoriesCallback callback
    ) {
        RetrofitClient.getApiService(applicationContext)
                .getCategories()
                .enqueue(new Callback<List<CategoryResponse>>() {
                    @Override
                    public void onResponse(
                            Call<List<CategoryResponse>> call,
                            Response<List<CategoryResponse>> response
                    ) {
                        if (!response.isSuccessful()) {
                            callback.onError(
                                    "Unable to load categories. HTTP "
                                            + response.code()
                            );
                            return;
                        }

                        List<CategoryResponse> responseCategories =
                                response.body();

                        if (responseCategories == null) {
                            callback.onError(
                                    "The server returned no category data."
                            );
                            return;
                        }

                        List<Category> categories =
                                mapResponses(responseCategories);

                        executorService.execute(() -> {
                            categoryDao.deleteAll();
                            categoryDao.upsertAll(categories);

                            mainHandler.post(
                                    () -> callback.onSuccess(categories)
                            );
                        });
                    }

                    @Override
                    public void onFailure(
                            Call<List<CategoryResponse>> call,
                            Throwable throwable
                    ) {
                        callback.onError(
                                throwable.getMessage() != null
                                        ? throwable.getMessage()
                                        : "Unable to connect to the server."
                        );
                    }
                });
    }

    /*
     * Reads the categories currently cached in Room.
     */
    public void getLocalCategories(
            CategoriesCallback callback
    ) {
        executorService.execute(() -> {
            List<Category> categories =
                    categoryDao.getAllCategories();

            mainHandler.post(
                    () -> callback.onSuccess(categories)
            );
        });
    }

    private List<Category> mapResponses(
            List<CategoryResponse> responses
    ) {
        List<Category> categories = new ArrayList<>();

        for (CategoryResponse response : responses) {
            categories.add(
                    new Category(
                            response.getId(),
                            response.getName(),
                            response.getColor()
                    )
            );
        }

        return categories;
    }

    /*
     * New callback interface for category-list operations.
     */
    public interface CategoriesCallback {

        void onSuccess(List<Category> categories);

        void onError(String message);
    }
}