package com.pigeonpost.android.utils;

import java.time.*;
import java.time.format.*;
import java.util.Locale;
import java.util.regex.*;

/** Render explicit offset timestamps in the phone's local zone, never infer an event date. */
public final class ReadableDateText {
    private ReadableDateText() {}
    private static final Pattern TIMESTAMP=Pattern.compile(
            "\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2}(?:\\.\\d{1,9})?)?(?:Z|[+-]\\d{2}:\\d{2})\\b");
    public static String format(String text) {return format(text,Locale.getDefault(),ZoneId.systemDefault());}
    public static String format(String text,Locale locale,ZoneId zone) {
        if(text==null) return "";
        var matcher=TIMESTAMP.matcher(text);var result=new StringBuffer();
        while(matcher.find()) {
            String replacement=matcher.group();
            try {
                var local=OffsetDateTime.parse(replacement).atZoneSameInstant(zone);
                replacement=DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(local)
                        +" at "+DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(local)
                        +" local time";
            } catch(DateTimeException invalid) { /* Preserve unparseable content, never invent a time. */ }
            matcher.appendReplacement(result,Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);return result.toString();
    }
}
