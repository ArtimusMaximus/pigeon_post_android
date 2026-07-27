package com.pigeonpost.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeonpost.android.R;
import com.pigeonpost.android.adapters.AiSourceAdapter;
import com.pigeonpost.android.data.remote.dto.AiAnswerResponse;
import com.pigeonpost.android.data.remote.dto.AiSourceResponse;
import com.pigeonpost.android.data.repository.AiRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AiActivity extends AppCompatActivity {

    private EditText questionInput;
    private Button speakQuestionButton;
    private Button askAiButton;
    private Button speakAnswerButton;
    private CheckBox autoSpeakCheckBox;
    private ProgressBar aiProgressBar;
    private TextView answerLabel;
    private TextView answerText;
    private TextView sourcesLabel;
    private RecyclerView sourcesRecyclerView;

    private AiRepository aiRepository;
    private AiSourceAdapter aiSourceAdapter;

    private TextToSpeech textToSpeech;
    private boolean textToSpeechReady;
    private boolean pendingAutoSpeak;

    private ActivityResultLauncher<Intent> speechRecognitionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ask PigeonPost AI");
        }

        initializeViews();
        initializeSourcesRecyclerView();
        initializeSpeechRecognition();
        initializeTextToSpeech();

        aiRepository = new AiRepository(this);

        speakQuestionButton.setOnClickListener(
                view -> startSpeechRecognition()
        );

        askAiButton.setOnClickListener(
                view -> askQuestion()
        );

        speakAnswerButton.setOnClickListener(
                view -> speakAnswer()
        );

        autoSpeakCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (!isChecked) {
                        pendingAutoSpeak = false;
                        stopSpeaking();
                    }
                }
        );
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    private void initializeViews() {
        questionInput =
                findViewById(R.id.questionInput);

        speakQuestionButton =
                findViewById(R.id.speakQuestionButton);

        askAiButton =
                findViewById(R.id.askAiButton);

        speakAnswerButton =
                findViewById(R.id.speakAnswerButton);

        autoSpeakCheckBox =
                findViewById(R.id.autoSpeakCheckBox);

        aiProgressBar =
                findViewById(R.id.aiProgressBar);

        answerLabel =
                findViewById(R.id.answerLabel);

        answerText =
                findViewById(R.id.answerText);

        sourcesLabel =
                findViewById(R.id.sourcesLabel);

        sourcesRecyclerView =
                findViewById(R.id.sourcesRecyclerView);
    }

    private void initializeSourcesRecyclerView() {
        aiSourceAdapter = new AiSourceAdapter();

        sourcesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        sourcesRecyclerView.setAdapter(
                aiSourceAdapter
        );

        sourcesRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initializeSpeechRecognition() {
        speechRecognitionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() != RESULT_OK
                                    || result.getData() == null) {
                                return;
                            }

                            ArrayList<String> results =
                                    result.getData()
                                            .getStringArrayListExtra(
                                                    RecognizerIntent.EXTRA_RESULTS
                                            );

                            if (results == null || results.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "No speech was recognized.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            questionInput.setText(results.get(0));

                            questionInput.setSelection(
                                    questionInput.getText().length()
                            );
                        }
                );
    }

    private void initializeTextToSpeech() {
        textToSpeech =
                new TextToSpeech(
                        this,
                        status -> {
                            if (status != TextToSpeech.SUCCESS) {
                                textToSpeechReady = false;
                                pendingAutoSpeak = false;

                                Toast.makeText(
                                        this,
                                        "Text-to-speech could not be initialized.",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            int languageResult =
                                    textToSpeech.setLanguage(
                                            Locale.getDefault()
                                    );

                            textToSpeechReady =
                                    languageResult
                                            != TextToSpeech.LANG_MISSING_DATA
                                            && languageResult
                                            != TextToSpeech.LANG_NOT_SUPPORTED;

                            if (!textToSpeechReady) {
                                pendingAutoSpeak = false;

                                Toast.makeText(
                                        this,
                                        "The selected language is not supported for speech.",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            if (pendingAutoSpeak
                                    && autoSpeakCheckBox.isChecked()
                                    && hasAnswer()) {

                                pendingAutoSpeak = false;
                                speakAnswer();
                            }
                        }
                );
    }

    private void startSpeechRecognition() {
        Intent speechIntent =
                new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                );

        speechIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        speechIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        speechIntent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Ask a question about your notes"
        );

        try {
            speechRecognitionLauncher.launch(speechIntent);
        } catch (Exception exception) {
            Toast.makeText(
                    this,
                    "Speech recognition is not available on this device.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void askQuestion() {
        String question =
                questionInput.getText()
                        .toString()
                        .trim();

        if (question.isEmpty()) {
            questionInput.setError(
                    "Enter a question about your notes."
            );

            questionInput.requestFocus();
            return;
        }

        pendingAutoSpeak = false;

        stopSpeaking();
        hideKeyboard();
        hidePreviousAnswer();
        setLoadingState(true);

        aiRepository.askQuestion(
                question,
                new AiRepository.AiCallback() {

                    @Override
                    public void onSuccess(
                            AiAnswerResponse response
                    ) {
                        runOnUiThread(() -> {
                            setLoadingState(false);

                            displayResponse(response);
                        });
                    }

                    @Override
                    public void onError(
                            int statusCode,
                            String message
                    ) {
                        runOnUiThread(() -> {
                            setLoadingState(false);

                            Toast.makeText(
                                    AiActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void displayResponse(
            AiAnswerResponse response
    ) {
        if (response == null) {
            Toast.makeText(
                    this,
                    "The AI returned an empty response.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        displayAnswer(response.getAnswer());
        displaySources(response.getSources());
    }

    private void displayAnswer(String answer) {
        String safeAnswer =
                answer == null
                        ? ""
                        : answer.trim();

        if (safeAnswer.isEmpty()) {
            safeAnswer =
                    "PigeonPost AI did not return an answer.";
        }

        answerText.setText(safeAnswer);

        answerLabel.setVisibility(View.VISIBLE);
        answerText.setVisibility(View.VISIBLE);
        speakAnswerButton.setVisibility(View.VISIBLE);

        if (!autoSpeakCheckBox.isChecked()) {
            return;
        }

        if (textToSpeechReady) {
            speakAnswer();
        } else {
            pendingAutoSpeak = true;
        }
    }

    private void displaySources(
            List<AiSourceResponse> sources
    ) {
        aiSourceAdapter.setSources(sources);

        boolean hasSources =
                sources != null
                        && !sources.isEmpty();

        sourcesLabel.setVisibility(
                hasSources
                        ? View.VISIBLE
                        : View.GONE
        );

        sourcesRecyclerView.setVisibility(
                hasSources
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void speakAnswer() {
        String answer =
                answerText.getText()
                        .toString()
                        .trim();

        if (answer.isEmpty()) {
            Toast.makeText(
                    this,
                    "There is no answer to read aloud.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!textToSpeechReady) {
            pendingAutoSpeak =
                    autoSpeakCheckBox.isChecked();

            Toast.makeText(
                    this,
                    "Text-to-speech is still preparing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        pendingAutoSpeak = false;

        textToSpeech.speak(
                answer,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "pigeonpost-ai-answer"
        );
    }

    private boolean hasAnswer() {
        return answerText.getText() != null
                && !answerText.getText()
                .toString()
                .trim()
                .isEmpty();
    }

    private void stopSpeaking() {
        if (textToSpeech != null
                && textToSpeech.isSpeaking()) {

            textToSpeech.stop();
        }
    }

    private void setLoadingState(boolean loading) {
        aiProgressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );

        askAiButton.setEnabled(!loading);
        speakQuestionButton.setEnabled(!loading);
        speakAnswerButton.setEnabled(!loading);
        questionInput.setEnabled(!loading);

        askAiButton.setText(
                loading
                        ? "Thinking..."
                        : "Ask AI"
        );
    }

    private void hidePreviousAnswer() {
        pendingAutoSpeak = false;

        answerLabel.setVisibility(View.GONE);
        answerText.setVisibility(View.GONE);
        speakAnswerButton.setVisibility(View.GONE);

        sourcesLabel.setVisibility(View.GONE);
        sourcesRecyclerView.setVisibility(View.GONE);

        answerText.setText("");

        aiSourceAdapter.setSources(null);
    }

    private void hideKeyboard() {
        View currentView = getCurrentFocus();

        if (currentView == null) {
            return;
        }

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        inputMethodManager.hideSoftInputFromWindow(
                currentView.getWindowToken(),
                0
        );
    }

    @Override
    protected void onDestroy() {
        pendingAutoSpeak = false;

        stopSpeaking();

        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}