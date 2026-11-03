package com.pigeonpost.android.utils;

import org.junit.Test;
import java.time.ZoneId;
import java.util.Locale;
import static org.junit.Assert.*;

public class PhoneFeedbackTest {
    @Test public void timestampBecomesLocalReadableWordsWithoutChangingRecordedLabel() {
        String answer=ReadableDateText.format("Recorded at 2026-11-03T01:30:22.123456Z; event date unknown.",
                Locale.US,ZoneId.of("America/Los_Angeles"));
        assertTrue(answer.contains("November 2, 2026"));assertTrue(answer.contains("5:30"));
        assertTrue(answer.contains("local time"));assertTrue(answer.contains("event date unknown"));
        assertFalse(answer.contains("T01:"));assertFalse(answer.contains("123456"));
        assertTrue(ReadableDateText.format("2026-11-03T01:30Z",Locale.US,ZoneId.of("America/Los_Angeles"))
                .contains("November 2, 2026"));
    }
    @Test public void offsetsAndDaylightSavingAreConvertedByZoneRules() {
        String a=ReadableDateText.format("2026-11-01T08:30:00Z",Locale.US,ZoneId.of("America/Los_Angeles"));
        String b=ReadableDateText.format("2026-11-01T01:30:00-07:00",Locale.US,ZoneId.of("America/Los_Angeles"));
        assertEquals(a,b);
        assertTrue(a.contains("1:30"));
    }
    @Test public void ordinaryDatesAndInvalidContentAreNotInventedOrRewritten() {
        String original="Meal on March 2; code P0420. Invalid: 2026-99-12T08:00:00Z";
        assertEquals(original,ReadableDateText.format(original,Locale.US,ZoneId.of("UTC")));
        assertEquals("",ReadableDateText.format(null));
    }
    @Test public void voiceAddsSegmentsAndClearStartsFresh() {
        assertEquals("Sushi and green tea",VoiceText.append("Sushi"," and green tea "));
        assertEquals("Sushi and tea",VoiceText.append("Sushi ","and tea"));
        assertEquals("New question",VoiceText.append("","New question"));
        assertEquals("New question",VoiceText.append("   ","New question"));
        assertEquals("Keep this",VoiceText.append("Keep this",null));
    }
}
