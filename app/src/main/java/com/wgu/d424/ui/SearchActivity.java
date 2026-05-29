package com.wgu.d424.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.wgu.d424.R;

public class SearchActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "pigeon_test_channel";
    public static final int NOTIFICATION_ID = 101;
    public static final int PERMISSION_REQUEST_CODE = 200;

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

        createNotificationChannel();

//    BACK BUTTON
        MaterialButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        });

//        NOTIFICATION TEST BUTTON
        MaterialButton testBtn = findViewById(R.id.btn_test_notification);
        testBtn.setOnClickListener(view -> {
            sendTestNotification();
        });

    }

    private void createNotificationChannel() {
        // Notification channels are only required on Android 8.0 (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test Channel";
            String description = "Channel for testing pigeon app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendTestNotification() {
        // 1. Create the intent to open the app on tap
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 2. Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_pigeon)
                .setContentTitle("Pigeon Post Delivered!")
                .setContentText("Your carrier pigeon has successfully arrived with a message.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 3. Get the Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // 4. CHECK PERMISSION (Fixes the Android Studio Warning)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                // If permission is missing, request it right now
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                return; // Stop execution so it doesn't try to notify without permission
            }
        }

        // 5. Issue the notification (Lint error is now gone!)
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}