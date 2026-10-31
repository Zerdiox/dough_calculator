# Tasks

## 1. Recipe model and weights

- [ ] 1.1 Write failing tests for `isLiquid` and `IngredientKind`, a liquid count refused, hydration (several liquids, none, above 200%, 62.25% shown as 62.3) and flour-only amounts, and for weights from ingredients alone; verify they fail for the expected reason
- [ ] 1.2 Add `isLiquid` to `Ingredient`, calculate hydration from the ingredient list, move the rounding text to a function of hundredths, remove the `hydration` field and `DoughAmounts.water`, sum hundredths in `amounts()`, make `DefaultDoughRecipe` flour only, and update test recipes; verify the 1.1 tests pass
- [ ] 1.3 Write failing `WeightRowsTest` cases (flour-only table, liquids after Flour, Water at 0% hidden), then remove the water row from `weightRows`; verify they pass

## 2. Stored data upgrade

- [ ] 2.1 Write failing `DoughDataFormatTest` cases from literal JSON for versions 1, 2 and 3: water becomes the first ingredient, a liquid with its decimals, 0% water left out, missing `currentRecipe` becomes the old default, stored `savedIngredients` gets Water and `isCount` becomes `kind`, missing stays default, and displayed weight rows match the values from before the upgrade
- [ ] 2.2 Add `upgradeToLiquidIngredients` and register it in `FormatUpgrades`; verify the 2.1 tests pass

## 3. Saved ingredients and name rules

- [ ] 3.1 Write failing tests for `SavedIngredient`'s kind (carried by `toSavedIngredient`, default list includes liquid Water, already-saved stays unchanged by saving a recipe, Undo keeps the kind), and for the repository action that replaces a saved ingredient, then implement them; verify they pass
- [ ] 3.2 Write failing `IngredientNameTest` cases ("Water" accepted, "Flour" and "Hydration" refused with their own messages), then update the name rules; verify they pass
- [ ] 3.3 Extract the kind choice and "Grams each" row into shared controls and add them to the saved ingredients screen, applying changes at once, with `SavedIngredientsScreenTest` cases (each kind, "Enter grams" keeps the last usable value, changing back restores grams each, name not editable, recipes untouched); verify they pass

## 4. Ingredient editor and calculator

- [ ] 4.1 Write failing `IngredientListEditorTest` cases (choosing Liquid, untouched row order while editing, liquids first after finishing, drag across groups regrouped, percentage kept between "%" and "Liquid", count and back restores, pick copies the kind, Undo keeps it, draft hydration), then implement them; verify they pass
- [ ] 4.2 Write failing `IngredientsCardTest` and `CalculatorEditModeTest` cases (read-only Hydration top row while viewing and editing, 0% with no ingredients, no separate water control, the `% | Liquid | Count` choice), then update the card; verify they pass
- [ ] 4.3 Update the saved-recipes summary to the new hydration with the existing format, with a test for Water 25% and Beer 40% reading "65% hydration"; verify it passes

## 5. Convert

- [ ] 5.1 Write failing `RecipeConversionTest` cases (no water field, flour alone, Liquid rows give hydration, liquids first in the recipe, rows keep screen order, changing the choice keeps what is typed, pick copies the kind), then update `ConversionDraft` and `ConvertedRow`; verify they pass
- [ ] 5.2 Remove the Water field from the Convert screen and add the `Grams | Liquid | Count` choice to rows, updating `ConvertRecipeScreenTest` and the preview; verify the tests pass

## 6. Final checks

- [ ] 6.1 Search main code for leftover water handling (`water`, `Water`, `hydration`) and confirm only the upgrade and the starting saved ingredient name water
- [ ] 6.2 Run `./gradlew check` and verify it is green
- [ ] 6.3 Run the instrumented tests on the Pixel_9 emulator and verify they pass
- [ ] 6.4 Manual checks on the emulator, starting from an install of the current version with saved data:
  - [ ] Update over the existing data: every saved recipe keeps its weights, with Water first as a liquid
  - [ ] Set Beer to Liquid on a recipe: after Done it moves under Hydration, and the total and saved-list summary update
  - [ ] Remove Water from a beer dough: Hydration stays correct and the weights table has no Water row
  - [ ] Hydration stays the top row while editing and changes when a row's choice changes
  - [ ] Change a saved Beer to Liquid and a saved Eggs to Count with 50 g each, then pick both in a recipe: they start that way, and recipes already using them are unchanged
  - [ ] Convert a book recipe with Water and Beer set to Liquid, then use it in the calculator and save it
  - [ ] Fresh install: Flour only, Hydration 0%, and Water offered as a liquid when adding an ingredient
- [ ] 6.5 Resolve F14 as described in `openspec/backlog/followup/README.md`, with `resolved_by: liquid-ingredients`, and verify the file is in `resolved/`

## 7. Follow-up harvest

- [ ] 7.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
