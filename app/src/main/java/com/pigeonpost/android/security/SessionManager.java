package com.pigeonpost.android.security;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.pigeonpost.android.ui.LoginActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class SessionManager {

    private final Context applicationContext;
    private final TokenManager tokenManager;

    private static final AtomicBoolean redirectInProgress =
            new AtomicBoolean(false);

    public SessionManager(Context context) {
        applicationContext =
                context.getApplicationContext();

        tokenManager =
                new TokenManager(applicationContext);
    }

    public void expireSession() {
        tokenManager.clearTokens();

        /*
         * Multiple requests may fail at the same time.
         * Only one should launch LoginActivity.
         */
        if (!redirectInProgress.compareAndSet(false, true)) {
            return;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(
                    applicationContext,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            applicationContext.startActivity(intent);
        });
    }

    public void markSessionActive() {
        redirectInProgress.set(false);
    }
}