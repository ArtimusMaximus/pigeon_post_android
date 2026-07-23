//package com.pigeonpost.android.utils;
//
//import android.app.Activity;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//import com.pigeonpost.android.R;
//import com.wgu.pigeonpost.android.MainActivity;
//
//public class PigeonNotification {
//    private static final String CHANNEL_ID = "pigeon_test_channel";
//    public static final int NOTIFICATION_ID = 101;
//    public static final int PERMISSION_REQUEST_CODE = 200;
//
//    private void createNotificationChannel() {
//        // Notification channels are only required on Android 8.0 (API 26) and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Test Channel";
//            String description = "Channel for testing pigeon app notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            // Register the channel with the system
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//    }
//
//    private void sendTestNotification(Context context, Activity activity) {
//        // 1. Create the intent to open the app on tap
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        // 2. Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification_pigeon)
//                .setContentTitle("Pigeon Post Delivered!")
//                .setContentText("Your carrier pigeon has successfully arrived with a message.")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        // 3. Get the Notification Manager
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager == null) return;
//
//        // 4. CHECK PERMISSION (Fixes the Android Studio Warning)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (androidx.core.content.ContextCompat.checkSelfPermission(context,
//                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
//
//                // If permission is missing, request it right now
//                androidx.core.app.ActivityCompat.requestPermissions(activity,
//                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
//                return; // Stop execution so it doesn't try to notify without permission
//            }
//        }
//
//        // 5. Issue the notification (Lint error is now gone!)
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
//    }
//}
