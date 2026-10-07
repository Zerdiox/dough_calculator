# Design

## Context

See proposal.md for why. The behaviour is in the four delta specs. Code paths below are relative to
`app/src/main/java/com/example/doughcalculator/`, the package after the app rename.

The design is shaped by these facts about the current code:

- `Percentage` stores whole hundredths. `FULL_PERCENT_HUNDREDTHS` (10 000) does two jobs: it is the
  100% denominator in `DoughRecipe`'s `fraction`, and it is the upper bound checked in
  `Percentage.init` and in `PercentRules` (`MAX`, `stepUp`, `canStepUp`).
- `DoughRepository.saveRecipe` always sets `loadedRecipeId` to the new recipe, and
  `DoughViewModel.loadRecipe` links the calculator to a saved recipe. Neither fits Convert, which
  must not link the calculator when saving and must clear the link when using a recipe.
- Building blocks already exist, but some are private: the save dialog with "Add to saved
  ingredients" (`SaveRecipeDialog`, `NewNamesChecklist` in `DoughCalculatorScreen.kt`), the name
  field with the saved-ingredients dropdown (`NameFieldWithSavedNames` in `IngredientsCard.kt`),
  the portions stepper (`PortionStepper` in `RecipeControls.kt`), the weights table
  (`DoughResultCard`), name rules (`ingredientNameError`), and the dropdown filter
  (`IngredientListEditor.offeredNames`).
- Temporary state that must survive rotation but not a restart already has a pattern:
  `IngredientEditing`, held by `DoughViewModel`.

## Goals / Non-Goals

**Goals:**
- Keep the conversion rules in plain Kotlin with no Android dependencies, so unit tests cover the
  math, rounding, precision and every rejection.
- Reuse the existing controls, so Convert looks and behaves like the rest of the app.

**Non-Goals:**
- No change to how weights are worked out from percentages, and no data upgrade.
- No drag-to-reorder on Convert rows.

## Decisions

### A separate upper limit for percentages
Add `MAX_PERCENT_HUNDREDTHS = 20_000` next to `FULL_PERCENT_HUNDREDTHS`. Use it in
`Percentage.init` and in `PercentRules` (`MAX` becomes 200, the error says "Enter 0 to 200").
`FULL_PERCENT_HUNDREDTHS` stays as the 100% that flour stands for.
*Why:* the two meanings only happened to share a number. Raising the shared constant would make
`fraction` wrong.
*Data:* every stored value is at most 10 000, so old data still passes the wider check. No upgrade
step is needed.

### Conversion as a pure draft class
Add `RecipeConversion.kt` with an immutable `ConversionDraft`. Like `IngredientListEditor`, every
change returns a new draft. The draft holds the flour text, the water text, the rows (key, name,
grams text), the portions and the "Add more" tick. It exposes:
- per-field messages: "Enter grams", "More than 0 g", "Over 200% of the flour", plus name errors
  from `ingredientNameError`, applied so that a name repeated from an earlier row is flagged on the
  later row, as in `IngredientListEditor.nameError`;
- a screen-level "Portions under 5 g" message;
- `toRecipe(): DoughRecipe?`, which is null while anything blocks conversion;
- the latest removal (the row and its position) with `undoRemove()` and `clearRemoval()`, the same
  shape as in `IngredientListEditor`. A newer removal replaces the older one. The screen shows
  "Removed "<name>"" through the existing `showUndoMessage` and calls `undoRemove()` or
  `clearRemoval()` with the result. Saving, using the recipe and opening Convert again all start
  from a draft with no pending removal, so Undo can't revive a row into a different recipe.

How grams are read: the text is trimmed, a comma becomes a point, and it must match digits with at
most two decimals (`\d{1,6}(\.\d{0,2})?|\.\d{1,2}`), read as `BigDecimal`.

How a percentage is formed: `hundredths = grams × 10 000 ÷ flour`, rounded half up to a whole
number. `decimals` is the scale of `hundredths / 100` after stripping trailing zeros, clamped to
0..2, so the value always lands on its own step. Anything above `MAX_PERCENT_HUNDREDTHS` is refused.
The portion weight is `floor(total ÷ portions)` with `RoundingMode.FLOOR`, and must be at least 5.

*Why `BigDecimal`:* the rounding has to be exact and half up, matching `PercentRules`. With doubles,
a case like 2.5 g in 1000 g could round the wrong way.
*Alternative:* reuse `IngredientListEditor` for the rows. Rejected, because its rows carry
percentages, and Convert rows carry grams text and are always newly added.

### Draft held by the ViewModel, reset on open
`DoughViewModel` gains a `conversion` holder, a small class like `IngredientEditing` with a
`StateFlow<ConversionDraft?>`. It offers `start(portions)`, which seeds the portions from the
calculator, `change(draft)` and `clear()`. Opening Convert calls `start`, so each visit starts
empty. The draft lives in memory only, so rotation keeps it and closing the app drops it.
*Alternative:* `rememberSaveable` with a custom `Saver`. Rejected, because it needs a `Saver` for a
nested list type, which is more code than the existing holder pattern, and that pattern is already
tested.

### Two new repository writes
- `saveRecipe` gains `linkCalculator: Boolean = true`. Convert passes `false`, so the new recipe is
  added with its ticked names but `loadedRecipeId` stays as it was. Everything else about saving
  stays shared.
- A new `useRecipe(recipe)` sets `currentRecipe` and clears `loadedRecipeId` in one `updateData`,
  so the calculator can never show a converted recipe while still linked to an old one.
  `DoughViewModel.useRecipe` sets `editedRecipe` straight away and writes through
  `writeCalculator` with `NonCancellable`, mirroring `loadRecipe`.

*Why a parameter instead of a second save method:* the ID creation and the name merge stay in one
place.

### Navigation
Add `ConvertKey` to `DoughCalculatorApp`. The calculator's top bar gets a third action, "Convert
recipe", that shares `actionsEnabled`, so it is off while editing. Then:
- Back pops `ConvertKey`.
- "Use in calculator" calls `useRecipe` and pops.
- Save with "Add more" off pops `ConvertKey` and pushes `FullRecipeKey(title = name, recipe)`. Back
  from the full recipe then lands on the calculator. The key carries the recipe itself, so the
  screen doesn't wait for the save to be written.
- Save with "Add more" on clears the draft (keeping the portions) and shows the snackbar on
  Convert.

### Reusing the UI pieces
Make these `internal` and move them to their own files, so Convert can use them unchanged:
`SaveRecipeDialog` and `NewNamesChecklist` go to `SaveRecipeDialog.kt`. `NameFieldWithSavedNames`
and `NameField` go to `IngredientNameField.kt`. `PortionStepper` stays in `RecipeControls.kt` and
becomes internal. The dropdown filter in `IngredientListEditor.offeredNames` becomes a top-level
function over "names used by other rows", so both screens share it.

The Convert screen, `ConvertRecipeScreen.kt`, has a Scaffold with a top bar and a back button. It
contains a card with the Flour and Water grams fields and the ingredient rows, a card with the
portions stepper, the weights table (`DoughResultCard`) while `toRecipe()` is not null, and the
"Add more" checkbox with both buttons. The grams fields are `CompactTextField`s with the decimal
keyboard and "g" as the suffix.

## Risks / Trade-offs

- [Preview differs slightly from the typed grams] → Rounding the percentages to hundredths and the
  portion weight down to whole grams means, for example, Flour 500 g previews as 499 g. Within a
  gram or two this doesn't matter in a kitchen, and the table shows what the calculator will
  actually make. The spec describes the table as the weights for the converted recipe, not as the
  typed grams.
- [200% makes typos easier to miss] → The calculator now accepts 150% where it used to refuse it.
  This was accepted on purpose for very wet doughs, and the weights table makes an odd value easy
  to see.
- [Moving private composables between files] → Behaviour must stay the same. The existing
  instrumented tests for the save dialog and the ingredients card cover it, and they must stay
  green.
- [An older app version reading data with values over 100%] → Not a concern: there is one device
  and no downgrade path.
