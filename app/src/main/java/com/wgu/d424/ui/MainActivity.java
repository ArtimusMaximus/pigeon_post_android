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

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 100;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextInputEditText editNote;

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

//        HANDLE RECORD NOTE BUTTON
        MaterialButton recordButton = findViewById(R.id.btnRecordNote);

//        NOTE TEXT AREA
        editNote = findViewById(R.id.editNote);

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
}