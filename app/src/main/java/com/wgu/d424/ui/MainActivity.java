package com.wgu.d424.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.wgu.d424.R;
import com.wgu.d424.data.dao.NoteDao;
import com.wgu.d424.data.db.AppDatabase;
import com.wgu.d424.data.entities.Note;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wgu.d424.adapters.RecentNotesAdapter;
import com.wgu.d424.data.db.AppDatabase;
import com.wgu.d424.data.entities.Note;
import com.wgu.d424.data.entities.Profile;
import com.wgu.d424.utils.SecurityUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 100;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextInputEditText editNote;
    private RecentNotesAdapter recentNotesAdapter;
    private RecyclerView recyclerRecentNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        GATHER RECENT NOTES
        recyclerRecentNotes = findViewById(R.id.recyclerRecentNotes);
        recentNotesAdapter = new RecentNotesAdapter(note -> {
            if (note.getIsPrivate()) {
                showPrivateKeyPrompt(note);
            } else {
                showNoteDialog(note);
            }
        });
        recyclerRecentNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecentNotes.setAdapter(recentNotesAdapter);
        loadRecentNotes();


//        GET DROPDOWN MENU
        AutoCompleteTextView categoryDropDownMenu = findViewById(R.id.dropdownCategory);
//        SET CATEGORY MENU OPTIONS
        String[] categories = { "Business", "Personal", "Idea", "Reminder", "Health", "Other" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>( MainActivity.this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryDropDownMenu.setAdapter(adapter);
//        SET STARTING CATEGORY
        categoryDropDownMenu.setText(categories[0], false);

//        NAVIGATE TO SEARCH NOTES ACTIVITY
        MaterialButton searchNotesNav = findViewById(R.id.btnSearchNotes);
        searchNotesNav.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

//        NAVIGATE TO PROFILE ACTIVITY
        ImageButton profileBtn = findViewById(R.id.btnProfile);

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
//        HANDLE RECORD NOTE BUTTON
        MaterialButton recordButton = findViewById(R.id.btnRecordNote);

//        NOTE TEXT AREA
        editNote = findViewById(R.id.editNote);

//        SET UP NATIVE SPEECH TO TEXT
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your note"
        );

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity.this, "Processing...", Toast.LENGTH_SHORT).show();
                resetTextAndTint(recordButton);
            }

            @Override
            public void onError(int error) {
                Toast.makeText(MainActivity.this, "Speech recognition error: " + error, Toast.LENGTH_SHORT).show();
                resetTextAndTint(recordButton);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    boolean categoryFound = false;

                    for (String c : categories) {
                        if (spokenText.toLowerCase().startsWith(c.toLowerCase())) {
                            categoryDropDownMenu.setText(c, false);
                            editNote.setText(spokenText.substring(c.length()).trim());
                            categoryFound = true;
                            break;
                        }
                    }
                    if (!categoryFound) {
                        editNote.setText(spokenText);
                    }
                }
                resetTextAndTint(recordButton);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        recordButton.setOnClickListener(v -> {
            startSpeechToText();
            setTextAndTint(recordButton);
        });

//        SAVE NOTE TO DATABASE
        MaterialButton saveNoteBtn = findViewById(R.id.btnSaveNote);
        saveNoteBtn.setOnClickListener(view -> {

            CheckBox privateChecked = findViewById(R.id.checkPrivateNote);
            boolean isPrivateChecked = privateChecked.isChecked();

            String category = categoryDropDownMenu.getText().toString().trim();
            String noteText = editNote.getText().toString().trim();

            if (noteText.isEmpty()) {
                Toast.makeText(
                        this,
                        "Enter a note, no empty notes are allowed!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            if (isPrivateChecked) {
                checkProfileAndSavePrivateNote(category, noteText, privateChecked);
            } else {
                saveNote(category, noteText, false, privateChecked);
            }
        });
    }

    private void startSpeechToText() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION
            );
            return;
        }
        speechRecognizer.startListening(speechRecognizerIntent);
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                Toast.makeText(this, "Microphone permission is required for voice notes.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    public void resetTextAndTint(MaterialButton recordButton) {
        recordButton.setText(R.string.btn_record_note);
        recordButton.setIconTint(
                ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.white)
                )
        );
    }
    public void setTextAndTint(MaterialButton recordButton) {
        recordButton.setText(R.string.misc_recording);
        recordButton.setIconTint(
                ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.red)
                )
        );
    }

    private void loadRecentNotes() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            List<Note> recentNotes = db.noteDao().getTopFiveRecentNotes();
            runOnUiThread(() -> { recentNotesAdapter.setNotes(recentNotes); });
        }).start();
    }
    private void  showNoteDialog(Note note) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(note.getCategory())
                .setMessage(note.getContent())
                .setPositiveButton("Close", null)
                .show();
    }
    private void saveNote(String category, String noteText, boolean isPrivate, CheckBox privateChecked) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);

            Note note = new Note(
                    category,
                    noteText,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    isPrivate
            );
            db.noteDao().insert(note);

            runOnUiThread(() -> {
                editNote.getText().clear();
                privateChecked.setChecked(false);
                Toast.makeText(this, "Note successfully saved to the database!", Toast.LENGTH_SHORT).show();
                loadRecentNotes();
            });
        }).start();
    }
    private void checkProfileAndSavePrivateNote(String category, String noteText, CheckBox privateChecked) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);

            boolean profileExists = db.profileDao().profileExists() > 0;
            runOnUiThread(() -> {
                if (!profileExists) {
                    showCreateSecurityProfileDialog(
                            category,
                            noteText,
                            privateChecked
                    );
                } else {
                    saveNote(
                            category,
                            noteText,
                            true,
                            privateChecked
                    );
                }
            });
        }).start();
    }

    private void showCreateSecurityProfileDialog(
            String category,
            String noteText,
            CheckBox privateChecked
    ) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(48, 16, 48, 0);

        TextInputEditText emailInput = new TextInputEditText(this);
        emailInput.setHint("Recovery email");
        emailInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        TextInputEditText pinInput = new TextInputEditText(this);
        pinInput.setHint("Create 4-digit PIN");
        pinInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );

        container.addView(emailInput);
        container.addView(pinInput);

        androidx.appcompat.app.AlertDialog dialog =
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Create Security Profile")
                        .setMessage("Private notes require a recovery email and 4-digit PIN.")
                        .setView(container)
                        .setPositiveButton("Save", null)
                        .setNegativeButton("Cancel", (d, which) -> {
                            privateChecked.setChecked(false);
                        })
                        .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String email = emailInput.getText() == null
                                ? ""
                                : emailInput.getText().toString().trim();

                        String pin = pinInput.getText() == null
                                ? ""
                                : pinInput.getText().toString().trim();

                        if (!SecurityUtils.isValidEmail(email)) {
                            emailInput.setError("Enter a valid email address");
                            return;
                        }

                        if (!SecurityUtils.isValidFourDigitPin(pin)) {
                            pinInput.setError("PIN must be exactly 4 digits");
                            return;
                        }

                        String pinHash = SecurityUtils.hashPin(pin);

                        new Thread(() -> {
                            AppDatabase db = AppDatabase.getDatabase(this);

                            Profile profile = new Profile(email, pinHash);
                            db.profileDao().saveProfile(profile);

                            runOnUiThread(() -> {
                                dialog.dismiss();

                                saveNote(
                                        category,
                                        noteText,
                                        true,
                                        privateChecked
                                );

                                Toast.makeText(
                                        this,
                                        "Security profile created.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                        }).start();
                    });
        });

        dialog.show();
    }
    private void showPrivateKeyPrompt(Note note) {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint("Enter 4-digit PIN");
        input.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle("Private Note")
                .setMessage("Enter your private PIN to view this note.")
                .setView(input)
                .setPositiveButton("Unlock", (dialog, which) -> {
                    String enteredPin = input.getText() == null
                            ? ""
                            : input.getText().toString().trim();

                    verifyPinAndShowNote(enteredPin, note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void verifyPinAndShowNote(String enteredPin, Note note) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            Profile profile = db.profileDao().getProfile();

            boolean isCorrect = profile != null
                    && profile.getPinHash() != null
                    && profile.getPinHash().equals(SecurityUtils.hashPin(enteredPin));

            runOnUiThread(() -> {
                if (isCorrect) {
                    showNoteDialog(note);
                } else {
                    Toast.makeText(this, "Incorrect PIN.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}