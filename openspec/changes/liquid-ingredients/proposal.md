# Proposal

## Why

Hydration tells the baker how easy a dough will be to work, but the app only counts water: a dough
with Beer 65% reads "0% hydration". Water also has rules no other ingredient has, which is what
causes this. Making every liquid an ordinary ingredient of a Liquid kind gives a true hydration
figure and removes those rules.

## What Changes

- The choice of an ingredient's kind gains **Liquid**, as `% | Liquid | Count` in the calculator's
  edit mode and `Grams | Liquid | Count` on Convert.
- Saved ingredients become editable: the saved ingredients screen gets the same kind choice and
  "Grams each" field as the recipe edit screen, in place of the "Count" text.
- Hydration becomes a calculated total: the sum of the liquids' percentages. It is shown on the
  saved-recipes summary and as a read-only top row of the calculator's ingredients, always, while
  editing too.
- **BREAKING (stored data, upgraded automatically):** water stops being a fixed part of a recipe. It
  becomes an ordinary ingredient that can be renamed, reordered or removed. Every existing recipe
  and the calculator's remembered values are upgraded with Water as a liquid, with all
  weights unchanged. Water at 0% is left out.
- Liquids always come first, directly under Hydration on the calculator and directly under Flour in
  the weights table, followed by everything else. Each group keeps its own order.
- Flour is the only default: a new recipe has no ingredients and Convert starts with only the Flour
  field and no rows.
- "Water" stops being a reserved name. "Flour" and "Hydration" stay reserved.
- Water joins the saved ingredients as a liquid, for new installs and through the upgrade.

## Resolves

- F14

## Out of Scope / Deferred

- F16: a Flour kind, so a recipe's flour can be split across several ingredients.
- A hydration line on the full recipe screen.
- A water-content factor per liquid: a liquid counts in full, so milk reads slightly high.
- Liquid count ingredients, such as eggs.
- Renaming a saved ingredient.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `ingredient-weights`: water becomes an ordinary ingredient; adds the Liquid kind, hydration and
  liquids-first order; "Water" is no longer a reserved name.
- `dough-calculator`: adds the Hydration row and the Liquid choice while editing; removes the water
  control; the default recipe has no ingredients.
- `saved-recipes`: recipes hold liquids and no separate water value; the summary shows the new
  hydration; the upgrade turns water into an ingredient.
- `saved-ingredients`: saved ingredients remember the Liquid kind and can be changed on their screen;
  Water joins the list.
- `convert-recipe`: removes the Water field; rows gain the Liquid choice.

## Impact

- **Stored data:** a new format version, with an upgrade for the calculator's recipe, every saved
  recipe and the saved ingredients.
- **Code:** the recipe and saved-ingredient models, weight calculation and table, name rules, the
  ingredient editor, the Convert draft and screen, the calculator's ingredients card, and the
  saved-recipes and saved-ingredients screens.
- **Tests:** unit and Compose tests that rely on a water field or the "Hydration" stepper.
- **Dependencies:** none.
