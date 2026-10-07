# Tasks

Paths are relative to `app/src/{main,test,androidTest}/java/com/example/doughcalculator/dough/`.

## 1. Preview rules

- [x] 1.1 In `RecipeConversionTest`, add failing tests for `ConversionDraft.preview()`:
  - Flour 500 and Water 325 with 4 portions, plus a new row with blank name and grams, previews
    4 × 206 g with water 65% and no ingredients.
  - The same with a blank-named row holding 15 g previews 4 × 206 g with no ingredients.
  - Flour 100, Water 60 and Salt 250 previews 4 × 40 g with water 60% and no Salt.
  - Flour 500 and Water "abc" previews water at 0%.
  - Flour 15 and Water 3 with 4 portions previews 4 × 4 g.
  - Blank Flour, Flour 0 and Flour "abc" each preview nothing.
  - For the book recipe (Flour 500, Water 325, Salt 15, Yeast 1), `preview()` equals `toRecipe()`.

  Verify they fail.
- [x] 1.2 Add `preview()` and rebuild `toRecipe()` on it as described in design.md, sharing the
  percentage and total logic. Verify 1.1 passes and every existing `RecipeConversionTest` case
  passes unchanged.

## 2. Convert screen

- [x] 2.1 In `ConvertRecipeScreenTest`, replace `blankNameHidesTheTableAndDisablesTheButtons` with
  a failing test: after typing Flour 500 and Water 325 and tapping "Add ingredient", "4 × 206 g"
  still shows and both buttons are disabled. Add a failing test that a blank Flour shows "Enter the
  flour weight to see the recipe" with both buttons disabled. Verify both fail with
  `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.doughcalculator.dough.ConvertRecipeScreenTest`.
- [x] 2.2 In `ConvertRecipeScreen.kt`, show `DoughResultCard` from `preview()` and the flour message
  card while it is null. Keep the buttons, the save dialog and "Use in calculator" on `toRecipe()`.
  Verify the whole `ConvertRecipeScreenTest` class passes.
- [x] 2.3 Run `./gradlew check` and verify it is green.

## 3. Manual checks on the Pixel_9 emulator

- [x] 3.1 Open Convert, type Flour 500, Water 325, tap "Add ingredient". The table stays and the
  screen doesn't jump. Type "Salt", then 15: Salt appears in the table once it has grams. Both
  buttons are enabled once the row is complete.
- [x] 3.2 Clear Flour. The table's place says "Enter the flour weight to see the recipe" and both
  buttons are disabled. Type 500 again and the table is back.
- [x] 3.3 Type 1500 for Salt. Its field says "Over 200% of the flour", Salt is gone from the table,
  the rest stays, and both buttons are disabled.
- [x] 3.4 With a complete recipe, "Use in calculator" still sends the same values as before.

## 4. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
