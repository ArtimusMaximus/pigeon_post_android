package com.pigeonpost.android.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                    .withZone(ZoneId.systemDefault());

    private DateTimeUtils() {
    }

    public static String formatForDisplay(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return "";
        }

        try {
            return DISPLAY_FORMAT.format(Instant.parse(timestamp));
        } catch (Exception exception) {
            return timestamp;
        }
    }
}