package com.wgu.d424.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.wgu.d424.R;
import com.wgu.d424.adapters.RecentNotesAdapter;
import com.wgu.d424.data.db.AppDatabase;
import com.wgu.d424.data.entities.Note;

import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText editSearchKeyword;
    private AutoCompleteTextView dropdownSearchCategory;
    private MaterialButton btnStartDate;
    private MaterialButton btnEndDate;
    private RecentNotesAdapter searchAdapter;

    private long startDateMillis = 0;
    private long endDateMillis = 0;

    // Temporary. Later, store/retrieve this from SharedPreferences.
    private static final String PRIVATE_KEY = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editSearchKeyword = findViewById(R.id.editSearchKeyword);
        dropdownSearchCategory = findViewById(R.id.dropdownSearchCategory);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);

        setupCategoryDropdown();
        setupRecyclerView();
        setupSearchListeners();
        setupDateButtons();

        MaterialButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(view -> finish());

        loadSearchResults();
    }

    private void setupCategoryDropdown() {
        String[] categories = {
                "All",
                "Business",
                "Personal",
                "Idea",
                "Reminder",
                "Health",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        dropdownSearchCategory.setAdapter(adapter);
        dropdownSearchCategory.setText(categories[0], false);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerSearchResults = findViewById(R.id.recyclerSearchResults);

        searchAdapter = new RecentNotesAdapter(note -> {
            if (note.getIsPrivate()) {
                showPrivateKeyPrompt(note);
            } else {
                showNoteDialog(note);
            }
        });

        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerSearchResults.setAdapter(searchAdapter);
    }

    private void setupSearchListeners() {
        editSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadSearchResults();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dropdownSearchCategory.setOnItemClickListener((parent, view, position, id) -> {
            loadSearchResults();
        });

//        MaterialButton btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(view -> loadSearchResults());
    }

    private void setupDateButtons() {
        btnStartDate.setOnClickListener(view -> showDatePicker(true));
        btnEndDate.setOnClickListener(view -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    if (isStartDate) {
                        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                        selectedDate.set(Calendar.MINUTE, 0);
                        selectedDate.set(Calendar.SECOND, 0);
                        selectedDate.set(Calendar.MILLISECOND, 0);

                        startDateMillis = selectedDate.getTimeInMillis();
                        btnStartDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                    } else {
                        selectedDate.set(Calendar.HOUR_OF_DAY, 23);
                        selectedDate.set(Calendar.MINUTE, 59);
                        selectedDate.set(Calendar.SECOND, 59);
                        selectedDate.set(Calendar.MILLISECOND, 999);

                        endDateMillis = selectedDate.getTimeInMillis();
                        btnEndDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                    }

                    loadSearchResults();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void loadSearchResults() {
        String keyword = editSearchKeyword.getText() == null
                ? ""
                : editSearchKeyword.getText().toString().trim();

        String category = dropdownSearchCategory.getText() == null
                ? "All"
                : dropdownSearchCategory.getText().toString().trim();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);

            List<Note> results = db.noteDao().searchNotes(
                    keyword,
                    category,
                    startDateMillis,
                    endDateMillis
            );

            runOnUiThread(() -> searchAdapter.setNotes(results));
        }).start();
    }

    private void showNoteDialog(Note note) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(note.getCategory())
                .setMessage(note.getContent())
                .setPositiveButton("Close", null)
                .show();
    }

    private void showPrivateKeyPrompt(Note note) {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint("Enter 4-digit key");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Private Note")
                .setMessage("Enter your private key to view this note.")
                .setView(input)
                .setPositiveButton("Unlock", (dialog, which) -> {
                    String enteredKey = input.getText() == null
                            ? ""
                            : input.getText().toString().trim();

                    if (enteredKey.equals(PRIVATE_KEY)) {
                        showNoteDialog(note);
                    } else {
                        Toast.makeText(this, "Incorrect private key.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}