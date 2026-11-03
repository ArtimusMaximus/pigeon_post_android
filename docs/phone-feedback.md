# Phone feedback fixes (1.1, versionCode 2)

Use this Android feature/phone-feedback branch with backend 536f246 or later
providing the optional relatedSources response field. Backend counterpart is
pigeon_post_server/docs/phone-feedback.md. Both repositories contain independent
commits; don't include the preexisting RetrofitClient.java URL edit with these changes.

## User-visible behavior

- Ask AI voice input appends recognized words to the existing question, adding a
  separator only when needed. Button says Speak for an empty question and Add more
  by voice for existing text. Clearing via the existing text-field clear icon or
  keyboard resets Speak. No checkbox/date-mode/source-selection workflow added.
- Home Record Note/Add more by voice/Clear implementation was already present and
  is unchanged. The reported missing behavior was confirmed on Ask AI.
- Explicit ISO offset timestamps in answer display/read-aloud are rendered as local
  date/time words (for example November 2, 2026 at 5:30 PM local time). Offset/zone
  rules handle day changes and DST; milliseconds and raw Z/offset strings are not
  spoken. Labels distinguishing recorded time from actual event time stay intact.
  Ordinary text and invalid timestamps are preserved; event dates aren't inferred.
- Answer sources and Other matching notes use separate lists. Related notes are
  browse candidates, not asserted evidence for the answer. Both lists hide when
  empty and older backend responses without relatedSources still work.
- Source cards show titles/local dates; tapping shows full note content and its ID,
  category and metadata, so a truncated preview doesn't hide the actual note.
- versionCode increased from 1 to 2 and versionName from 1.0 to 1.1 for updates.
  Keep the same application ID and signing key as the installed version.

## Verification

Six standalone JVM JUnit/Gson tests compiled with Java 11 compatibility pass:
voice joining/cleared input, local-time conversion including midnight/DST and
minute-only ISO values, preserving non-date/invalid content, old response tolerance,
and separation of answer/related source fields. Android resource XML parses.

Full Gradle :app:testDebugUnitTest/:app:assembleDebug was attempted. Wrapper cache
access needed escalation; after that, SDK resolution failed because local.properties
contains a Windows SDK path that WSL cannot resolve. No machine-specific SDK files
were edited. These checks do not establish Android activity/resource compilation,
visual behavior, recognizer callback behavior, or phone TTS success. Build and test
in the existing Android Studio Windows setup before installing the signed APK.
No APK was produced by the agent.

## Manual checkpoint

1. Deploy the backend companion image through the normal workflow, preserving
   production hybrid/Qwen environment settings. No migration needed.
2. Keep the phone's production URL in RetrofitClient.java; its existing local
   change was preserved. Build this branch in Android Studio; sign using the
   installed app's key. Install as an update without uninstalling.
3. On Ask AI, dictate a partial question, then Add more by voice. Verify the first
   words survive. Clear and dictate a replacement; no old question should return.
4. Ask about foods/drinks across the last times at a place with two notes. Expect
   separately attributed recent recorded passages and multiple answer-source cards,
   not date clarification or a request to identify a note.
5. View other matching notes when available and tap a card to inspect full content.
6. Read an answer containing recorded metadata aloud. Confirm natural local date/
   time and preserved uncertainty about actual event timing.
7. Check typed Ask AI, home recording/append/Clear, and ordinary note saving.

Manual phone tests and Android Studio build remain pending. The backend live model
check used only synthetic restaurant notes; no personal account/data was accessed.
