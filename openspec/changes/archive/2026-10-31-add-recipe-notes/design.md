# Design

## Context

See proposal.md for why. The calculator's values live in one recipe value object, held by the
ViewModel and written to DataStore as JSON after a short pause. The full recipe receives a copy of
that object through its navigation key. Stored data is decoded with unknown keys ignored, and a
property with a default may be missing from older files.

The project's Compose BOM maps to Compose 1.12.1 (read by position from Google's BOM mapping table),
and `compose-rich-editor` 1.2.0 moved to Compose 1.12 and AGP 9.

## Goals / Non-Goals

**Goals:**
- Typed lists that are formatted while editing and on the full recipe, using an existing library.
- Older stored data loads unchanged.

**Non-Goals:**
- Custom Markdown parsing or list handling of our own.
- Defining which formatting beyond lists works.

## Decisions

**The note is a property of the recipe, not of the saved recipe entry.** A `notes: String = ""`
property on `DoughRecipe` means Update, Save as new, load, remembering, navigation to the full recipe,
Undo after delete, and the temporary portion copies on the full recipe all carry it with no extra code.
Convert builds a recipe without one, which gives the specified clearing. Keeping it on the saved entry
was rejected because the calculator edits the note and has no saved entry when nothing is loaded.

**No stored-format upgrade step.** The default covers files without the key, so the format version
doesn't change. An older-format test pins this.

**`compose-rich-editor` for both screens.** `OutlinedRichTextEditor` is the field, and a read-only
`RichText` is the display, so both format lists identically. Its list typing shortcuts are documented
behaviour, so no toolbar or list code is ours. Alternatives: Google's Compose rich text input has
styling building blocks but no lists; `AztecEditor-Android` is built on `EditText` and stores HTML; a
plain field with a Markdown renderer wouldn't format while typing.

**Stored as the editor's HTML.** `toHtml()` and `setHtml()` give back exactly what was typed, including
symbols such as `*` and `#`. Markdown was rejected after testing it on the emulator: it added a blank
line between text and a list on every reload, and changed text such as `2 * 3 * 4`. A blank field is
stored as no note.

**The rich text state lives in a small Notes composable and reports out through the recipe.** The
composable creates the state with `rememberSaveable` and the library's saver, starting from the note it
is given, and sends `toHtml()` up through the existing recipe change callback when
the text changes. The flow is one way: the editor owns the text while editing, and never reads the
recipe back. Only replacing the calculator's recipe as a whole, by loading or using a converted recipe,
gives it new text. The ViewModel exposes the recipe together with a revision that those two actions
increase, in one value, so they can't arrive out of step, and the calculator is wrapped in `key(revision)`
at the app's wiring, so a new revision starts it fresh from that recipe. Comparing the recipe's note
with the editor on each change was rejected: the recipe lags typing, so telling an old echo from a newly
loaded recipe could only be a guess. The
library has no change callback, so changes are observed from its `annotatedString` snapshot state. Its
saver restores HTML and the selection, so rotation keeps the cursor.

**Notes stays available while ingredients are being edited.** A recipe edit covers both. Done copies
only the edited ingredients onto the calculator's recipe as it stands at that moment, so a note changed
during the edit is kept. A test pins this.

## Risks / Trade-offs

- [The library doesn't build with this project's Kotlin 2.4.20 or Compose 1.12.1] → The first task is
  a test build, and the change stops for Kenny's decision if it fails.
- [The state's restore after rotation isn't confirmed, because its `Saver` wasn't read] → An
  instrumented rotation test covers it.
- [Writing HTML on each text change could cost typing speed] → The ViewModel already debounces
  writes, and a note is short. Checked in the manual checklist.
- [A bottom field could sit under the keyboard] → The calculator body already has `imePadding`, and
  the manual checklist covers it.
- [The editor may not match other Material 3 fields] → It offers an outlined variant, and colours are
  checked on the emulator and passed in if they differ.
