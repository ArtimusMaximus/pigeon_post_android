package com.wgu.d424.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    private Spinner spinnerSearchCategory;
    private MaterialButton btnStartDate;
    private MaterialButton btnEndDate;
    private RecentNotesAdapter searchAdapter;

    private long startDateMillis = 0;
    private long endDateMillis = 0;

    private static final String PRIVATE_KEY = "1234";

    private final String[] searchCategories = {
            "All",
            "Business",
            "Personal",
            "Idea",
            "Reminder",
            "Health",
            "Other"
    };

    private final String[] noteCategories = {
            "Business",
            "Personal",
            "Idea",
            "Reminder",
            "Health",
            "Other"
    };

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
        spinnerSearchCategory = findViewById(R.id.spinnerSearchCategory);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);

        setupCategorySpinner();
        setupRecyclerView();
        setupSearchListeners();
        setupDateButtons();
        setupNavigationButtons();

        loadSearchResults();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                searchCategories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSearchCategory.setAdapter(adapter);
        spinnerSearchCategory.setSelection(0);
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
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                loadSearchResults();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spinnerSearchCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {
                        loadSearchResults();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    private void setupDateButtons() {
        btnStartDate.setOnClickListener(view -> showDatePicker(true));
        btnEndDate.setOnClickListener(view -> showDatePicker(false));
    }

    private void setupNavigationButtons() {
        MaterialButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(view -> finish());

        MaterialButton reportBtn = findViewById(R.id.btn_report);
        reportBtn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, ReportActivity.class);
            startActivity(intent);
        });
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

        String category = spinnerSearchCategory.getSelectedItem() == null
                ? "All"
                : spinnerSearchCategory.getSelectedItem().toString();

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
                .setNegativeButton("Edit", (dialog, which) -> {
                    showEditNoteDialog(note);
                })
                .setNeutralButton("Delete", (dialog, which) -> {
                    confirmDeleteNote(note);
                })
                .show();
    }

    private void showEditNoteDialog(Note note) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(32, 16, 32, 0);

        Spinner categorySpinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                noteCategories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(adapter);

        for (int i = 0; i < noteCategories.length; i++) {
            if (noteCategories[i].equalsIgnoreCase(note.getCategory())) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        TextInputEditText noteInput = new TextInputEditText(this);
        noteInput.setHint("Note");
        noteInput.setText(note.getContent());
        noteInput.setMinLines(4);
        noteInput.setMaxLines(8);
        noteInput.setGravity(android.view.Gravity.TOP);

        CheckBox privateCheckBox = new CheckBox(this);
        privateCheckBox.setText("Private note");
        privateCheckBox.setChecked(note.getIsPrivate());

        container.addView(categorySpinner);
        container.addView(noteInput);
        container.addView(privateCheckBox);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Edit Note")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedCategory =
                            categorySpinner.getSelectedItem().toString();

                    String updatedContent = noteInput.getText() == null
                            ? ""
                            : noteInput.getText().toString().trim();

                    if (updatedContent.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Note cannot be empty.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    note.setCategory(updatedCategory);
                    note.setContent(updatedContent);
                    note.setIsPrivate(privateCheckBox.isChecked());
                    note.setUpdatedAt(System.currentTimeMillis());

                    updateNote(note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateNote(Note note) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            db.noteDao().update(note);

            runOnUiThread(() -> {
                Toast.makeText(
                        this,
                        "Note updated successfully.",
                        Toast.LENGTH_SHORT
                ).show();

                loadSearchResults();
            });
        }).start();
    }
    private void confirmDeleteNote(Note note) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to permanently delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteNote(note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteNote(Note note) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            db.noteDao().delete(note);

            runOnUiThread(() -> {
                Toast.makeText(
                        this,
                        "Note deleted successfully.",
                        Toast.LENGTH_SHORT
                ).show();

                loadSearchResults();
            });
        }).start();
    }

    private void showPrivateKeyPrompt(Note note) {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint("Enter 4-digit key");
        input.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle("Private Note")
                .setMessage("Enter your private key '1234' to view this note.")
                .setView(input)
                .setPositiveButton("Unlock", (dialog, which) -> {
                    String enteredKey = input.getText() == null
                            ? ""
                            : input.getText().toString().trim();

                    if (enteredKey.equals(PRIVATE_KEY)) {
                        showNoteDialog(note);
                    } else {
                        Toast.makeText(
                                this,
                                "Incorrect private key.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}