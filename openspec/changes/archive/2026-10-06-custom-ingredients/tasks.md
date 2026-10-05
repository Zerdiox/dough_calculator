# Tasks

Apply `undo-delete-recipe` first. Run `./gradlew spotlessApply` after creating any new `.kt` file
(F10).

## 1. Value rules

- [x] 1.1 Add `PercentageTest`: typed text sets decimals counting trailing zeros (`60` → 0,
  `0.7` → 1, `3.10` → 2); more than two decimals rounds half up (`0.125` → 0.13, two decimals);
  a comma counts as a point; blank, `abc`, `.` and `120` are errors with "Enter 0 to 100"; stepping
  adds or takes one step without drift (0.1 × 3 = 0.3) and stays in 0–100 keeping the decimals;
  field text shows the decimals (`0.00`); table text drops trailing zeros (`0.2`, `62`); hint reads
  "± 1", "± 0.1", "± 0.01". Verify the tests fail.
- [x] 1.2 Add `Percentage` (hundredths + decimals) and `PercentRules`. Verify 1.1 passes.
- [x] 1.3 Add `GramsRulesTest`: whole grams only; below 5, blank or 7+ digits is an error with
  "At least 5 g"; 252 stays 252; + goes to 255 and − to 250 from 252; − from 250 is 245; − is
  disabled at 5; hint "± 5 g". Verify the tests fail.
- [x] 1.4 Add the `ValueRules` interface and `GramsRules`; make `PercentRules` implement it.
  Verify 1.1 and 1.3 pass.
- [x] 1.5 Add `IngredientNameTest` for `ingredientNameError`: blank is blank (no message);
  trimmed; case-insensitive duplicate gives ""Salt" is already in this recipe"; Flour, Water and
  Hydration in any case give "Flour and water are part of every recipe already"; 31 characters is
  refused; "Salt after" next to "Salt before" is fine. Implement it and verify the tests pass.

## 2. Recipe model

- [x] 2.1 Update `DoughRecipeTest` for the new model: the weights for 1000 g with water 65%,
  Salt 3%, Yeast 0.2% (flour about 594.5 g, sum 1000 g); doubling portions doubles every weight;
  the default recipe is 4 × 250 g, water 62% (0 decimals) and no ingredients. Verify they fail to
  compile or fail.
- [x] 2.2 Change `DoughRecipe` to `hydration: Percentage` and `ingredients: List<Ingredient>`,
  keeping the stored portion keys, and update `DefaultDoughRecipe` and every caller enough to
  compile (temporary UI wiring is fine; groups 5–7 replace it). Verify 2.1 and earlier unit tests
  pass.
## 3. Weights table

- [x] 3.1 Add `WeightRowsTest` (F7): rows are Flour, Water, then the ingredients in order; a 0%
  ingredient and 0% water are left out; weights under 10 g show one decimal (1.19 → "1.2 g"),
  others whole grams (594.5 → "595 g"), half up; percentages show at most two decimals without
  trailing zeros ("62%", "0.25%", "5.1%" for 3.10). Verify the tests fail.
- [x] 3.2 Add `weightRows(recipe)` and build the result card's rows from it. Verify 3.1 passes.

## 4. Stored data and saved ingredients

- [x] 4.1 Extend `DoughDataFormatTest`: the existing version-1 JSON upgrades so both recipes have
  water with 0 decimals, Salt (1 decimal) then Yeast (2 decimals), and no Olive oil because it was
  0%; olive oil at 4.3% becomes Olive oil 4.3 with 1 decimal after Yeast; a hand-edited 62.5% water
  keeps 62.5 with 1 decimal; written data records version 2; data without `savedIngredients` reads
  as Olive oil, Salt and Yeast; the weights table text (via `weightRows` from 3.2) is the same before and after the upgrade. Update the
  tests that assumed version 1 as the current version. Verify the new tests fail.
- [x] 4.2 Add the version-1 → 2 upgrade to `FormatUpgrades` and `savedIngredients` with its
  default to `DoughData`. Verify 4.1 and all format tests pass.
- [x] 4.3 Extend `DoughRepositoryTest`: saving a recipe with names to add stores the recipe and
  the names in one write (the file holds both); updating the loaded recipe with names does the
  same; adding a name already saved in another case changes nothing; deleting a saved ingredient
  and adding it back works; deleting a saved ingredient leaves recipes that use it unchanged.
  Verify the tests fail.
- [x] 4.4 Add the repository and ViewModel functions for saving with names, deleting and
  restoring a saved ingredient, and a `savedIngredients` flow sorted case-insensitively. Verify
  4.3 and all unit tests pass.

## 5. Shared value control

- [x] 5.1 Add `ValueStepperTest` (androidTest): the label, field text, unit and hint show; tapping
  + and − reports the stepped value; − is disabled at the bottom and + at the top; typing a valid
  value reports it; typing an invalid value shows the error and reports nothing; leaving the field
  after an invalid value shows the last accepted value again; a value changed from outside updates
  the field. Verify it fails on the `Pixel_9` emulator with `connectedDebugAndroidTest`.
- [x] 5.2 Add the `ValueStepper` composable (compact field about 96 dp, decimal or number
  keyboard, hint and error under the field) using `ValueRules` and the shared `StepButton`. Verify
  5.1 passes on `Pixel_9`.
- [x] 5.3 Update `PortionsCardTest`: the portion weight shows "250", "g" and "± 5 g", and + makes
  255. Replace the portion weight slider in the Portions card with `ValueStepper` and `GramsRules`,
  and remove the portion weight's slider scale. Verify the
  test passes on `Pixel_9`; the full recipe screen picks this up without changes.

## 6. Ingredients card and edit mode

- [x] 6.1 Add `IngredientListEditorTest`: starting from a recipe gives one row per ingredient;
  rename, move, add (empty name, 0% with 0 decimals, marked as added) and remove work; undo puts a
  removed row back at its index with its name and percentage; a second remove replaces the undo;
  `apply()` is null while any name is blank or invalid and otherwise returns the trimmed list in
  order; the dropdown offers saved names not in the draft, filtered by `contains` ignoring case,
  alphabetically. Verify the tests fail.
- [x] 6.2 Add `IngredientListEditor`. Verify 6.1 passes.
- [x] 6.3 Extend `DoughViewModelTest`: start editing exposes an editor built from the calculator
  recipe; Done with a valid editor updates the calculator recipe and ends editing; Done with an
  invalid editor changes nothing; editing does not change a saved recipe. Verify it fails, then
  add the ViewModel's editing state and functions and verify it passes.
- [x] 6.4 Add the Reorderable library to `libs.versions.toml` and the app module after confirming
  its latest version and `ReorderableColumn` / `draggableHandle` API against the Compose BOM in use.
  Verify `./gradlew assembleDebug` succeeds.
- [x] 6.5 Add `IngredientsCardTest` (androidTest): outside edit mode, Hydration and each ingredient
  show a `ValueStepper`, and the Edit button shows; in edit mode, Hydration shows "In every recipe",
  each ingredient shows a name field, a drag handle and a remove button, Add ingredient adds a
  row, a duplicate name shows its message and disables Done, and a blank name disables Done with
  no message; the "Move up" and "Move down" accessibility actions reorder rows; a new row's
  dropdown lists the offered saved names and picking one fills the name. Verify it fails on
  `Pixel_9`.
- [x] 6.6 Replace the baker's percentages card with the Ingredients card: `ValueStepper` rows with
  `PercentRules`, Edit/Done, the edit rows with `ReorderableColumn`, drag handles and accessibility
  actions, the remove button, Add ingredient, and the exposed dropdown with an editable field on
  added rows (check the anchor-type API in the BOM's Material 3). Remove `LabeledSlider`,
  `SliderScale` and the remaining slider scales. Verify 6.5 passes on `Pixel_9`.
- [x] 6.7 In the calculator screen: show "Removed "<name>"" with Undo after a removal (dismissing
  any current message first, `SnackbarDuration.Long`), wired to the editor's undo; end the message
  on Done; disable Save, the saved recipes button and Start kneading while editing; make Back call
  Done when it is enabled. Add these cases to an androidTest for the calculator content and verify
  it passes on `Pixel_9`.

## 7. Saving

- [x] 7.1 Extend `LoadedRecipeSaveTest` (androidTest): the name dialog lists ingredient names not
  yet saved under "Add to saved ingredients", unticked, and reports the ticked ones on Save; the
  update-or-save-as-new dialog does the same and carries ticks into the name dialog; with no new
  names, no list shows; Cancel reports nothing. Verify it fails on `Pixel_9`.
- [x] 7.2 Add the checklist to both save dialogs, hold the ticked set in `DoughCalculatorContent`,
  and change the save callbacks to pass the names; wire them in `PizzaApp` to the ViewModel
  functions from 4.4. Verify 7.1 passes on `Pixel_9`.
- [x] 7.3 Add `SavedIngredientsScreenTest` (androidTest): the title "Saved ingredients"; names
  listed as given; the empty message when there are none; delete reports the name and shows
  "Deleted "<name>"" with Undo; Undo reports the restore; a second delete replaces the message.
  Verify it fails on `Pixel_9`.
- [x] 7.4 Add the saved ingredients screen, its navigation key and entry in `PizzaApp`, a new
  vector icon from Material Symbols, and a top-bar action on the saved recipes screen that opens
  it. Verify 7.3 passes on `Pixel_9`, and with `./gradlew assembleDebug`.

## 8. Build and manual check

- [x] 8.1 Run `./gradlew check` and verify it is green.
- [x] 8.2 Install the app over an existing install on the `Pixel_9` emulator that has at least two
  saved recipes from the current version (one with olive oil above 0%), and check:
  - [x] Saved recipes show the same weights as before the update; olive oil at 0% is gone, olive
    oil above 0% is listed after Yeast
  - [x] `adb shell run-as com.example.pizza cat files/datastore/dough.json` shows `"version":2`
    after one change
  - [x] Type 0.7 for Yeast: + and − move by 0.1, the hint reads "± 0.1"; type 0.25: moves by 0.01;
    step down to 0: the field shows "0.00"
  - [x] Type "abc" and 120: "Enter 0 to 100" shows and the table doesn't change; leaving the field
    restores the value
  - [x] Portion weight: type 252, tap + → 255; type 1200 → the table follows; type 3 → "At least
    5 g"; at 5 g − is disabled
  - [x] Edit: rename Salt, drag Yeast above it, remove an ingredient and Undo, add a row and pick
    from the dropdown, add "salt" again and see Done disabled; Done applies everything
  - [x] While editing, Save, saved recipes and Start kneading are disabled; Back acts as Done
  - [x] Save a recipe with Honey ticked: Honey shows on the saved ingredients screen; save another
    with Truffle oil unticked: it doesn't
  - [x] Saved ingredients: delete Salt and Undo; delete Yeast: recipes with Yeast keep it, and the
    dropdown no longer offers it
  - [x] Delete a saved ingredient and go back before tapping Undo: it stays deleted
  - [x] Start kneading: the full recipe shows the ingredients, and its portion weight field works
    like the calculator's
  - [x] Rotate the phone while editing: the draft stays
  - [x] Force-stop the app while editing and reopen: the calculator shows its values from before
    editing
  - [x] On a fresh install (`adb uninstall`, then install): 4 × 250 g, Hydration 62%, no other
    ingredients; saved ingredients are Olive oil, Salt, Yeast

## 9. Follow-up harvest

- [x] 9.1 List each out-of-scope issue discovered during implementation as a follow-up candidate
  in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
