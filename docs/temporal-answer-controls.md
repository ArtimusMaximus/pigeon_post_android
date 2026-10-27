# Explicit date-question controls checkpoint

Android branch: feature/temporal-answer-controls. Requires server API ad821ee or
compatible successor for temporal behavior/source selection. Older APIs may ignore
unknown request fields, so a successful HTTP response alone does not prove these
controls are honored. Use the compatible local API during acceptance. No migration
or backend restart needed if that image is already running; rebuild Android.

The AI screen defaults to General question (STANDARD), with explicit choices for
When something happened (EVENT_DATE) and When a note was recorded (RECORDED_DATE).
No automatic natural-language classification. Retrofit sends answerMode and
selectedSourceIds using existing JSON response fields. General questions select
no sources. Existing repository entry point still sends STANDARD.

After a date response with sources, Choose source notes and ask again opens a
multi-selection dialog with source IDs, titles, content previews and recorded
metadata. At least one note is required; selection resubmits the original question
and mode. All source cards remain available. Source choice disappears if the
question/mode differs from the displayed response; Ask AI always starts a fresh
unselected query. Controls disable during network requests. Backend rejects IDs
not found by the current user-scoped retrieval; changed notes can require a new
query rather than silently falling back. Backend raw timestamp text remains as-is.

## Verification

Two standalone JVM/Gson request tests passed: STANDARD serialization, numeric
selected IDs and defensive selection copy. XML parsing/whitespace checks passed.
Gradle assembleDebug was attempted but blocked because local.properties points to
a Windows SDK path unavailable to WSL. Full Android build, visual review, emulator
network interaction and rotation testing are pending; no manual passes claimed.

In Android Studio, rebuild and exercise:

1. General question retains previous behavior and voice/read-aloud/source details.
2. Event-date question with unknown date returns uncertainty and labeled recorded
   context; a note-recorded question uses metadata, not an inferred event date.
3. Ambiguous recorded-date answer offers source choice. Select the intended note
   and verify only its recorded date enters the answer; all cards remain visible.
4. Select multiple notes for an event question and verify IDs/context stay distinct.
5. Empty selection stays in dialog with a message; Cancel sends no request.
6. Editing the question or changing mode hides old selection; no stale IDs go to
   a fresh question. Confirm typing, voice replacement and clearing behave alike.
7. Network/session failure recovers controls and reports the failure. Verify
   duplicate submission is blocked during loading and source choice after retry.
8. Portrait/landscape and enlarged fonts: dropdown, dialog content and response
   scrolling remain usable. Rotation currently follows existing activity behavior;
   results/selection are not newly persisted across activity recreation.

No server changes, production configuration changes or APK artifacts in this
increment. Deployed frontend testing needs the compatible deployed server first.
