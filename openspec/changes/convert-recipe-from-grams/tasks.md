# Tasks

Paths are relative to `app/src/{main,test,androidTest}/java/com/example/doughcalculator/dough/`.
Start only once `rename-app-to-dough-calculator` is committed.

## 1. Percentages up to 200%

- [ ] 1.1 In `PercentageTest`, add failing tests: a `Percentage` of 20 000 hundredths is allowed and
  20 001 is refused; `PercentRules` parses "150" and "200", refuses "200.01" and "250" with "Enter 0
  to 200", stops `stepUp` at 200, and `canStepUp` is false at 200 and true at 100. Verify they fail.
- [ ] 1.2 Add `MAX_PERCENT_HUNDREDTHS = 20_000` in `Percentage.kt`. Use it in `Percentage.init` and in
  `PercentRules` (`MAX`, error text, `stepUp`, `canStepUp`), keeping `FULL_PERCENT_HUNDREDTHS` as the
  100% denominator. Verify 1.1 passes.
- [ ] 1.3 In `DoughRecipeTest`, add a test that water at 110% gives flour = total ÷ 2.1 and weights
  adding up to the total. In `DoughDataFormatTest` (or `DoughDataStoreTest`), add a test that a
  stored recipe at exactly 100% still reads back unchanged. Verify both pass.
- [ ] 1.4 Update any instrumented test that expects "Enter 0 to 100" or a + button disabled at 100%
  (for example `ValueStepperTest` and `IngredientsCardTest`) to the 200% behaviour. Verify they
  compile with `./gradlew compileDebugAndroidTestKotlin`.

## 2. Conversion rules

- [ ] 2.1 Create `RecipeConversionTest` with failing tests for `ConversionDraft`:
  - Flour 500, Water 325, Salt 15, Yeast 1 with 4 portions gives water 65% (step 1), Salt 3%
    (step 1), Yeast 0.2% (step 0.1), and 4 × 210 g.
  - Flour 600 and Yeast 1 gives 0.17% with step 0.01.
  - "1,5" reads as 1.5 g. "abc" and "1.255" say "Enter grams". Flour 0 says "More than 0 g". A
    blank Flour gives no recipe and no message. A blank Water counts as 0%.
  - Flour 100 and Water 250 flags Water with "Over 200% of the flour".
  - Weights totalling 18 g with 4 portions say "Portions under 5 g" and give no recipe.
  - A named row with blank or 0 grams is left out. A tiny amount that rounds to 0% is kept at 0%.
  - A blank name blocks the recipe with no message. "salt" after "Salt" flags the later row with
    ""Salt" is already in this recipe". "Water" is refused with the reserved-name message.
  - Rows keep their order in the recipe. Removing a row drops it. "Add more" clearing keeps the
    portions and the tick and empties everything else.
  - Removing the middle of three rows and undoing puts it back in the middle with its name and
    grams. A second removal replaces the first, so Undo restores only the second. `clearRemoval()`
    ends the Undo, and the "Add more" clear leaves no pending removal.

  Verify they fail.
- [ ] 2.2 Implement `RecipeConversion.kt` (`ConversionDraft` with `BigDecimal` grams parsing,
  percentages rounded half up to hundredths, derived decimals, portion weight rounded down, per-field
  and screen messages, `toRecipe()`) as described in design.md. Verify 2.1 passes.
- [ ] 2.3 Extract the saved-names filter from `IngredientListEditor.offeredNames` into a shared
  top-level function and use it from both `IngredientListEditor` and `ConversionDraft`. Add a
  `RecipeConversionTest` case for the dropdown offering only names not used by other rows. Verify
  `IngredientListEditorTest` and `RecipeConversionTest` pass.

## 3. Saving and using a converted recipe

- [ ] 3.1 In `DoughRepositoryTest`, add failing tests: `saveRecipe(..., linkCalculator = false)`
  adds the recipe and the ticked names but leaves `loadedRecipeId` and `currentRecipe` unchanged;
  `useRecipe(recipe)` sets `currentRecipe` and clears `loadedRecipeId` in one write. Verify they fail.
- [ ] 3.2 Implement the `linkCalculator` parameter and `useRecipe` in `DoughRepository`. Verify 3.1
  passes and the existing repository tests stay green.
- [ ] 3.3 In `DoughViewModelTest`, add failing tests: `conversion.start(6)` gives an empty draft with
  6 portions, and starting again after changes is empty again; `useRecipe` shows the recipe on
  `recipe` at once and clears `loadedRecipe`; saving a converted recipe leaves `loadedRecipe` as it
  was. Then add the conversion holder and the `useRecipe`/save functions to `DoughViewModel` and
  verify the tests pass.

## 4. Convert screen and navigation

- [ ] 4.1 Move `SaveRecipeDialog` and `NewNamesChecklist` to `SaveRecipeDialog.kt`, move
  `NameFieldWithSavedNames` and `NameField` to `IngredientNameField.kt`, and make them and
  `PortionStepper` `internal`, with no change in behaviour. Verify the existing instrumented tests
  for the calculator, edit mode and saving still pass (`./gradlew connectedDebugAndroidTest` on the
  Pixel_9 emulator).
- [ ] 4.2 Write `ConvertRecipeScreenTest` (instrumented) first, covering: the screen shows "Convert
  recipe", empty Flour and Water fields and both buttons disabled; typing 500/325 with 4 portions
  shows "4 × 206 g"; a blank-named row disables both buttons and hides the table; "Add more" starts
  unticked; Save opens the name dialog with "Add to saved ingredients" for a new name; removing the
  Salt row shows "Removed "Salt"" and Undo brings it back. Verify they fail.
- [ ] 4.3 Build `ConvertRecipeScreen.kt` as described in design.md: top bar with Back, grams fields
  with "g" and the decimal keyboard, ingredient rows with the name dropdown and remove, the portions
  stepper, the weights table, the "Add more" checkbox, and both buttons, with snackbar support,
  including the "Removed" message with Undo through `showUndoMessage`.
  Verify 4.2 passes.
- [ ] 4.4 Add `ConvertKey` and the "Convert recipe" top-bar action (disabled while editing) in
  `DoughCalculatorApp`/`DoughCalculatorScreen`. Wire Back, "Use in calculator", Save with "Add more"
  off (replace Convert with the new recipe's `FullRecipeKey`) and on (clear, keep portions, snackbar
  "Saved "<name>""). Add a `CalculatorEditModeTest` case that the Convert action is disabled while
  editing, and verify it passes.
- [ ] 4.5 Run `./gradlew check` and verify it is green (unit tests, lint, Detekt).

## 5. Manual checks on the Pixel_9 emulator

- [ ] 5.1 Convert the book recipe and check each step:
  - [ ] Open Convert from the calculator's top bar. The portions match the calculator's.
  - [ ] Type Flour 500, Water 325, add Salt 15 (picked from the dropdown) and Yeast "1,5". The table
    shows Water 65%, Salt 3%, Yeast 0.3% and "4 × 210 g".
  - [ ] Type Water 1100. The field says "Over 200% of the flour" and both buttons are disabled.
  - [ ] Rotate the phone. The input is kept.
  - [ ] Tap "Use in calculator". The calculator shows the values, and Save offers only a new recipe.
- [ ] 5.2 Check saving:
  - [ ] Load "Friday night", open Convert, enter a recipe, add "Malt", Save as "Book A" with Malt
    ticked and "Add more" off. The full recipe "Book A" opens. Back lands on the calculator, which
    still shows Friday night's values, and Save still offers to update "Friday night". Malt is in
    the saved ingredients.
  - [ ] Open Convert, tick "Add more", save "Book B". Convert stays, the fields are empty, the
    portions and the tick are kept, and the message says "Saved "Book B"".
  - [ ] Open Convert, add three rows with grams, remove the middle one, and tap Undo. It is back in
    the middle with its name and grams.
  - [ ] Open Convert, type something, go back, open again. It is empty.
- [ ] 5.3 Check the 200% limit on the calculator: + goes past 100% for Hydration, stops at 200%, and
  typing 250 says "Enter 0 to 200".
- [ ] 5.4 Check that Convert can't be opened while editing the ingredients.

## 6. Follow-up harvest

- [ ] 6.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
