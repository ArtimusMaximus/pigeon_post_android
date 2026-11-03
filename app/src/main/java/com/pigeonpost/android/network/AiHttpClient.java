package com.pigeonpost.android.network;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

/** AI inference has a longer bounded wait; ordinary requests retain their own deadlines. */
public final class AiHttpClient {
    private AiHttpClient() {}
    public static OkHttpClient from(OkHttpClient authenticated) {
        return authenticated.newBuilder()
                .readTimeout(120,TimeUnit.SECONDS)
                .callTimeout(150,TimeUnit.SECONDS)
                .build();
    }
}
