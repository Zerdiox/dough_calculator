# Tasks

## 1. Keep stored data readable through the rename

- [x] 1.1 Add a unit test that decodes a stored-data JSON string in today's format (current recipe
  and one saved recipe, using the keys `pizzaCount` and `ballWeightGrams`) and checks the count and
  weight, and that encoding a recipe still writes those keys. Verify it passes against today's code.
- [x] 1.2 Rename the recipe model's `pizzaCount` to `portionCount` and `ballWeightGrams` to
  `portionWeightGrams` with the IDE's rename refactoring. Add `@SerialName` with the old keys and a
  one-line comment saying why. Update the model's KDoc so it no longer says "pizza". Verify the test
  from 1.1 still passes without editing its JSON.
- [x] 1.3 Rename the unit tests that say pizzas (`zeroPizzasNeedNoDough`,
  `moreSavedPizzasScaleEveryIngredient`) to portion wording, keeping what they assert. Verify the
  unit tests pass.

## 2. Portions card: minimum and wording (F1, F2)

- [x] 2.1 Add a Compose UI test for the portions card: it shows "Portions" and "Portion weight"; at 1
  portion the "Fewer portions" button is disabled and at 2 it is enabled; at 50 "More portions" is
  disabled. Verify it fails on the current code with `connectedDebugAndroidTest` on an emulator or
  device.
- [x] 2.2 On the portions card, change the title to "Portions", the subtitle to "How many, and how
  heavy", the weight label to "Portion weight", and the stepper's descriptions to "Fewer portions"
  and "More portions". Verify the test from 2.1 now finds every label and button, and fails only on
  − being enabled at 1.
- [x] 2.3 Give the stepper a minimum of 1 next to the maximum of 50 and enable − only above the
  minimum. Verify the test from 2.1 passes.
- [x] 2.4 Rename the remaining portion-concept code names: the card (`PizzaSizeCard`), the stepper
  (`PizzaCountStepper`) and its animation label, the maximum constant (`MAX_PIZZAS`), the weight
  scale (`BallWeightScale`) and the full recipe's local count and weight state. Leave the package,
  application ID, `PizzaApp` and the theme names alone. Verify that searching the Kotlin sources for
  `pizzaCount`, `ballWeight`, `Pizzas`, `pizzas` and `Dough ball` finds only the `@SerialName`
  keys and the JSON in the stored-format test, and the test from 2.1 still passes.
- [x] 2.5 In the README's description, replace "how many pizzas you want and how heavy each dough
  ball should be" with portion wording. Verify the README reads correctly.

## 3. Hydration in the saved recipes summary (F8)

- [x] 3.1 Change the saved recipe summary to "<portions> × <weight> g · <hydration>% hydration".
  Verify in the app that a recipe with 4 portions of 250 g and 62% water shows
  "4 × 250 g · 62% hydration".

## 4. Build and manual check

- [x] 4.1 Run `./gradlew check` and verify it is green.
- [x] 4.2 Install the app over the existing install (don't uninstall) and check:
  - [x] Recipes saved before the update are all still there with the same portions, portion weight
    and hydration
  - [x] The calculator opens with the values it had before the update
  - [x] The card reads "Portions", "How many, and how heavy" and "Portion weight"
  - [x] At 1 portion the − button is disabled on the calculator; at 50 the + button is disabled
  - [x] On the full recipe ("Start kneading"), the − button is disabled at 1 as well
  - [x] The saved recipes list shows "… · 62% hydration" style summaries
  - [x] The weights table still labels the ingredient row "Water"

## 5. Follow-up harvest

- [x] 5.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
