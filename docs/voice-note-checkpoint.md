# Append voice notes checkpoint

Android branch: `feature/append-voice-notes`. Compatible with the existing server
API at `f8e398d` (documentation baseline `bf3be5e`); no API/schema changes.

Voice results append to the current draft, preserving typed text. The voice
button reads “Add more by voice” when the draft has text and “Record Note” when
empty. Clear stays visible and is enabled only when the draft contains text. Clear cancels pending recognition and clears only draft content.
Category prefixes are recognized only for an empty draft; later segments preserve
the selected category and their spoken text.

## Manual acceptance (pending)

Rebuild Android; no backend restart, migration, or model change is needed.
In portrait and landscape:

1. Start empty: Record Note is shown and Clear is visible but disabled.
2. Type text: Clear becomes enabled and the voice label becomes Add more by voice.
3. Record two segments: each appends once with spacing; original text remains.
4. Clear: draft empties, Clear disables, Record Note returns, category/privacy remain unchanged.
5. Delete text manually: controls return to the empty state.
6. Deny microphone permission or trigger no-speech/error: draft remains intact
   and the button recovers. Grant permission and retry.
7. Clear during recognition: late results do not restore the cleared draft.
8. Rotate with a draft: restored text and controls agree.
9. Save a synthetic draft successfully: text and controls reset as before.

Verification: both layout XML files parsed successfully. Emulator/microphone and
visual acceptance have not been performed by the agent. Build result is recorded
in the task handoff.
