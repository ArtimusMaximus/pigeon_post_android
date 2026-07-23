package com.pigeonpost.android.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.pigeonpost.android.R;
import com.pigeonpost.android.data.db.AppDatabase;
import com.pigeonpost.android.data.entities.Profile;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.pigeonpost.android.utils.SecurityUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtUserEmail;
    private Profile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUserEmail = findViewById(R.id.txtUserEmail);

        MaterialButton backBtn = findViewById(R.id.btnBackProfile);
        backBtn.setOnClickListener(view -> finish());

        loadProfile();

        MaterialButton editEmailBtn = findViewById(R.id.btnEditRecoveryEmail);
        editEmailBtn.setOnClickListener(view -> showEditRecoveryEmailDialog());
    }

    private void loadProfile() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            Profile profile = db.profileDao().getProfile();

            runOnUiThread(() -> {
                currentProfile = profile;

                if (profile == null || profile.getEmail() == null || profile.getEmail().isEmpty()) {
                    txtUserEmail.setText("No recovery email set");
                } else {
                    txtUserEmail.setText(profile.getEmail());
                }
            });
        }).start();
    }
    private void showEditRecoveryEmailDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(48, 16, 48, 0);

        TextInputEditText emailInput = new TextInputEditText(this);
        emailInput.setHint("Recovery email");
        emailInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (currentProfile != null && currentProfile.getEmail() != null) {
            emailInput.setText(currentProfile.getEmail());
        }

        container.addView(emailInput);

        androidx.appcompat.app.AlertDialog dialog =
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Edit Recovery Email")
                        .setMessage("Enter the recovery email used for private note recovery.")
                        .setView(container)
                        .setPositiveButton("Save", null)
                        .setNegativeButton("Cancel", null)
                        .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String newEmail = emailInput.getText() == null
                                ? ""
                                : emailInput.getText().toString().trim();

                        if (!SecurityUtils.isValidEmail(newEmail)) {
                            emailInput.setError("Enter a valid email address");
                            return;
                        }

                        updateRecoveryEmail(newEmail);
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }
    private void updateRecoveryEmail(String newEmail) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);

            Profile profile = db.profileDao().getProfile();

            if (profile == null) {
                profile = new Profile(newEmail, "");
            } else {
                profile.setEmail(newEmail);
            }

            db.profileDao().saveProfile(profile);

            Profile updatedProfile = profile;

            runOnUiThread(() -> {
                currentProfile = updatedProfile;
                txtUserEmail.setText(updatedProfile.getEmail());

                Toast.makeText(
                        this,
                        "Recovery email updated.",
                        Toast.LENGTH_SHORT
                ).show();
            });
        }).start();
    }
}