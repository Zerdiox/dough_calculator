# Proposal

## Why

A recipe can't hold free text, so there's nowhere to keep things like a flour blend or the steps for
making the dough. The user has to remember them.

## What Changes

- Every recipe can have a note, kept with its other values.
- The calculator gets a Notes field at the very bottom, below "Start kneading". Typing `-` or `1.` at
  the start of a line makes a bullet or numbered list as you type. There is no formatting toolbar.
- The full recipe shows the note, formatted, under the weights table, and shows nothing when there is
  no note. This includes the full recipe opened with "Start kneading".
- The note is saved by "Update" and "Save as new" like any other value, is remembered by the
  calculator, and is replaced when a recipe is loaded or "Use in calculator" is chosen.
- Recipes saved before this update have no note, and all their values stay the same.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `dough-calculator`: adds the Notes field, editable alongside the ingredients, and remembers it.
- `full-recipe`: shows the note under the weights table.
- `saved-recipes`: a saved recipe holds a note, and loading one replaces the calculator's note.
- `convert-recipe`: "Use in calculator" clears the calculator's note, because a converted recipe has
  none.

## Resolves

- F13

## Out of Scope / Deferred

- A note field in the save dialog: notes are written only on the calculator.
- A Notes field on the Convert screen: a converted recipe gets its note once loaded into the
  calculator.
- Editing a note on the full recipe, or showing notes on the saved recipes list.
- A formatting toolbar, and supporting other formatting as a feature.

## Impact

- **Dependency:** adds `compose-rich-editor`, a third-party Compose library (Apache 2.0). Google's
  Compose rich text input has no ready-made lists. Whether the library builds against this project is
  the first task, and the change stops for a decision if it doesn't.
- **Stored data:** a new optional value on each recipe and on the calculator. Older data still loads.
- **Code:** the recipe model, calculator screen, full recipe screen, and their tests. README.
