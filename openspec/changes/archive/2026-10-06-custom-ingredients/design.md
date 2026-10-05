# Design

## Context

See proposal.md for why. The relevant parts of the app today:

- A recipe has fixed fields for hydration, salt, yeast and oil as `Float`s. The weights table builds
  a fixed list of five rows from them.
- Every control on the Portions and Ingredients cards is assembled separately from a shared slider
  and `StepButton`. The full recipe screen reuses the Portions card.
- Stored data is one JSON file with a format version. Older data is upgraded one version at a time
  by functions in a list that is still empty. Tests check that stored data keeps loading.
- The calculator writes its values after a short pause. Saving, updating and loading recipes go
  through the repository in one `updateData` each, so linked values always agree.
- The calculator is a scrolling `Column`, not a lazy list.
- `undo-delete-recipe` adds a message with Undo to the saved recipes screen (dismiss the current
  message, show with `SnackbarDuration.Long`, act on `ActionPerformed`). This change assumes it is
  applied first and reuses that pattern.

## Goals / Non-Goals

**Goals:**
- Stepping a percentage never drifts (0.1 + 0.1 + 0.1 is exactly 0.3).
- The control and its rules each live in one place. Portion weight, hydration and every ingredient
  use them, on both the calculator and the full recipe.
- The rules for values, names and the edited list are plain Kotlin with unit tests. UI tests cover
  wiring only.
- After the upgrade, every stored recipe shows the same weights as before.

**Non-Goals:**
- Keeping unfinished edits across an app restart.
- Undo for removed ingredients after Done.

## Decisions

### A percentage is stored as whole hundredths plus its number of decimals
`Percentage(hundredths: Int, decimals: Int)`, where 2.5% with one decimal is `(250, 1)`. Two
decimals is the finest precision, so hundredths hold every value exactly. Stepping adds 1, 10 or
100 hundredths, which is plain integer maths. The field text, the step hint, and the table's
"at most two decimals, no trailing zeros" all come from these two numbers. Weights are still worked
out in floating point from `hundredths / 100`.

- *Alternative: `Float` value plus decimals.* Repeated steps of 0.1 drift (0.30000001), so every
  step would need rounding again, and equality in tests gets fuzzy.
- *Alternative: `BigDecimal`.* Exact, but needs a custom serializer and carries the scale
  separately anyway.

### The recipe holds water's percentage and an ordered list of named ingredients
`DoughRecipe(portionCount, portionWeightGrams, hydration: Percentage, ingredients:
List<Ingredient>)`, with `Ingredient(name, percentage)`. Flour is not stored. The portion keys keep
their stored names (`pizzaCount`, `ballWeightGrams`). Names are unique within a recipe, so the
weights table and Compose keys can use them outside edit mode.

### Saved ingredients are a list of names in the stored data
`DoughData.savedIngredients: List<String>`, defaulting to Olive oil, Salt and Yeast. Data stored
before this change has no such key, so it decodes to the default. That covers "updating users get
the starting list" with no upgrade code. Order on disk doesn't matter: the list is sorted
(case-insensitively) where it is shown. Adding is case-insensitively idempotent, so a repeated Undo
or tick can't create a duplicate.

### Format version 2: the first entry in the upgrade list
The upgrade rewrites `currentRecipe` and every `savedRecipes[].recipe` in the JSON:

- `hydrationPercent` becomes `hydration` with decimals 0.
- `saltPercent`, `yeastPercent` and `oilPercent` become `ingredients` entries named Salt, Yeast and
  Olive oil, in that order, with decimals 1, 2 and 1. Entries at 0 are left out.
- A file with no `currentRecipe` (defaults aren't written) gets version 1's default recipe before
  converting, so a calculator that was never changed keeps showing salt and yeast instead of
  falling back to version 2's water-only default.
- Hundredths are the old float × 100 rounded to the nearest whole number. If a stored value needs
  more decimals than its default (a hand-edited 62.5% water), the decimals go up to fit, so no
  value changes.

It works on the JSON rather than decoding the old model, so the old recipe class doesn't have to be
kept around. The upgrade test feeds in the same version-1 JSON the format tests already use.

### One value control, driven by a rules object
`ValueRules` is a small interface with two implementations, `PercentRules` (0–100, precision from
the typed decimals) and `GramsRules` (whole grams, at least 5, six digits at most, ± 5 snapping to
the grid). It covers parsing typed text into a value or an error message, stepping up and down,
whether each button is enabled, the field text and the hint text. One composable,
`ValueStepper(label, value, rules, onValueChange)`, draws the row: the name on the left, then −, a
compact field (about 96 dp) with the unit, and +, with the hint and error underneath. The portion
count keeps its own stepper but shares `StepButton`.

The field keeps the typed text locally while it has focus, and reports only accepted values. On
losing focus it shows the formatted accepted value again. When the value changes from outside (the
buttons, loading a recipe), the text follows. A comma is read as a decimal point, because some
keyboards only offer a comma.

- *Alternative: one composable per kind of value.* That's the duplication the user asked to avoid.
- *Alternative: keep the slider next to the field.* It can't reach small values across 0–100.

### Editing is a plain list editor held by the ViewModel
`IngredientListEditor` is an immutable value holding draft rows (a key that lives only while
editing, the draft name, the percentage, and whether the row was added in this edit), plus the
last removal for Undo. It offers rename, move, add, remove, undo-remove, validation per row, and
`apply(): List<Ingredient>?` (null while any name is invalid). Name validation lives in its own
function, `ingredientNameError(name, otherNames)`, which the save path also uses.

The ViewModel holds `MutableStateFlow<IngredientListEditor?>` (null when not editing). It survives
rotation, and is lost when the app closes, which matches the spec. Done applies the list to the
calculator recipe through the existing `updateRecipe`, so it is written like any other change.

- *Alternative: `rememberSaveable` in the screen.* It would need a `Saver` for the rows, and the
  logic would be harder to test without Compose.
- *Alternative: persist the draft.* That's more stored state for a rare case. The spec accepts
  losing unfinished edits.

While editing, the screen disables Save, the saved recipes button and "Start kneading", and a
`BackHandler` is active for the whole edit: it calls Done when Done is enabled and does nothing
otherwise, so Back can't leave the app with an invalid name.

### Drag to reorder with the Reorderable library
`sh.calvin.reorderable` provides `ReorderableColumn` with a `draggableHandle()` modifier, for
non-lazy columns. That fits the calculator's scrolling `Column`, where a nested lazy list isn't
allowed. Rows also get custom accessibility actions "Move up" and "Move down" that call the
editor's move. At apply, confirm the latest version (3.1.0 was current when this was planned) and
its API against the Compose BOM in use.

- *Alternative: hand-rolled `pointerInput` dragging.* More code to own, and the coding principles
  prefer a library.
- *Alternative: move-up and move-down buttons only.* Rejected in the prototype.

### Picking saved ingredients uses Material 3's exposed dropdown with an editable field
Only rows added in this edit use `ExposedDropdownMenuBox` with an editable anchor. The menu items
are the saved names not in the draft list, filtered by `contains`, ignoring case. At apply, check
the anchor-type API name in the Material 3 version from the BOM.

### Promotion happens in the same write as the save
The calculator works out the "new names": recipe ingredient names not among the saved ingredients,
ignoring case. The ticked set is held in `DoughCalculatorContent`, so it carries from the
update-or-save-as-new dialog into the name dialog. The callbacks become
`onSaveRecipe(name, namesToSave)` and `onUpdateLoadedRecipe(namesToSave)`. The repository adds the
names in the same `updateData` as the recipe, so a recipe can't be saved without its ticked names
or the other way round.

### Saved ingredients screen
This is a new navigation key and screen, opened from a top-bar action on the saved recipes screen.
It has a `LazyColumn` of names, each with a delete button. Delete removes the name at once and
shows "Deleted "<name>"" with Undo, using the same pattern as `undo-delete-recipe`. Undo adds the
name back. Because the list is always sorted, there's no position to restore. The action needs a
new vector icon from Material Symbols.

### Table rows come from a pure function
The weights table builds its rows from `weightRows(recipe): List<WeightRow>` (name, percent text,
grams text), which leaves out 0 g rows. F7's tests for rounding, percentage text and hidden rows
test that function without Compose.

## Risks / Trade-offs

- [Upgraded floats such as `0.2f` become `0.2` exactly, so weights may differ in the eighth decimal
  place] → Only the shown weights must be the same. The upgrade test compares the table text before
  and after.
- [The Reorderable library or the dropdown API differs from what was planned] → Both are checked
  first in their tasks. If Reorderable doesn't fit, fall back to hand-rolled drag on the handle
  with the same accessibility actions.
- [Six-digit portion weights × 50 portions] → At most 49 999 950 g, which fits in an `Int`.
- [Typing into a field while the debounced write runs] → Only accepted values reach the ViewModel,
  and they go through the same debounced write as before.
- [The edit draft is lost if the app is killed mid-edit] → Accepted in the spec. Edits are short.
- [Both this change and `undo-delete-recipe` touch the saved recipes screen] → Apply
  `undo-delete-recipe` first; this change only adds a top-bar action there.

## Migration Plan

The upgrade runs when stored data is first read after the update, and the data is written back as
version 2 on the next write. An older app can't read version 2 and would treat it as unreadable;
the existing unreadable-data path keeps a copy. A downgrade is not supported, as before.
