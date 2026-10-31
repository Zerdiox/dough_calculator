# Tasks

## 1. Library check

- [x] 1.1 Add `com.mohamedrejeb.richeditor:richeditor-compose:1.2.0` to the version catalog and the app, and verify `./gradlew assembleDebug` succeeds. If it fails on Kotlin or Compose versions, stop and report to Kenny.
- [x] 1.2 Read the library source for `RichTextState.Saver`, `toMarkdown`, `setMarkdown`, `OutlinedRichTextEditor` and `RichText`, and note in the summary anything that contradicts design.md.

## 2. Note on the recipe

- [x] 2.1 Write failing tests: stored data without a note decodes to an empty note with other values unchanged; a note survives a store and read; update and save as new keep the note; loading replaces it; restoring a deleted recipe keeps it; a converted recipe has no note. Verify they fail for the expected reason.
- [x] 2.2 Add the `notes` property with an empty default to the recipe and verify the 2.1 tests pass.

## 3. Calculator Notes field

- [x] 3.1 Write failing tests: lists written through the library and typed symbols read back the same after storing, with no blank line added; the field is below "Start kneading"; typed text reaches the recipe; a loaded recipe's note replaces the field; a note changed while editing ingredients is kept after Done, along with the ingredient changes; the note survives rotation.
- [x] 3.2 Add the Notes composable and place it last on the calculator, and verify the 3.1 tests pass.

- [x] 3.3 Write tests through the real app and ViewModel: loading replaces an unsaved note, using a converted recipe clears the note, Update stores the changed note, and leaving and returning keeps the note. Verify they pass, and that they fail when the calculator's `key(revision)` is taken out.
- [x] 3.4 Make the flow one way: the ViewModel exposes the recipe and its revision as one value, the app wraps the calculator in `key(revision)`, and the field starts from the note it is given and only reports changes. Verify all notes tests pass.

## 4. Full recipe display

- [x] 4.1 Write failing tests: a note appears under the weights table with lists formatted; nothing appears without a note; changing portions leaves the note; there is no way to edit it. "Start kneading" showing the calculator's note is a manual check, since it runs through app navigation.
- [x] 4.2 Show the note read-only under the weights table and verify the 4.1 tests pass.
- [x] 4.3 Describe notes in the README and verify it matches the specs.

## 5. Final checks

- [x] 5.1 Run `./gradlew check` and verify it is green.
- [x] 5.2 Manual checks on the Pixel_9 emulator:
  - [x] Type `- ` and `1. ` in Notes: lists form as you type, Enter continues them, and Enter on an empty item ends them.
  - [x] Long list lines wrap under their own text on the calculator and the full recipe.
  - [x] The keyboard doesn't cover the Notes field.
  - [x] Typing a long note feels smooth (checked by Kenny on his phone).
  - [x] Notes looks like the app's other fields (the app has no dark theme, and stays light in night mode).
  - [x] Rotate while typing: the text and lists stay.
  - [x] Close and reopen the app: the note is still there.
  - [x] Save as new, Update and load all carry the note, and an unsaved change doesn't reach the saved recipe.
  - [x] An existing recipe from before the update opens with no note and the same weights.
  - [x] "Start kneading" shows the calculator's note under the weights table.
  - [x] "Use in calculator" from Convert clears the note.
- [x] 5.3 Resolve F13 as described in `openspec/backlog/followup/README.md` and verify the file moved to `resolved/` with `resolved_by: add-recipe-notes`.

## 6. Follow-up harvest

- [x] 6.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
