# Proposal

## Why

Some ingredients, such as eggs, are counted rather than weighed. As a percentage of the flour they
scale to amounts nobody can measure, like 37 g of egg, and Convert reads "2" as 2 g.

## What Changes

- Any ingredient other than flour and water can be either a percentage or a count, set per
  ingredient on the calculator's ingredient editor and on Convert's rows.
- A count scales with the number of portions and shows to the nearest half, never below 0.5 unless
  it is 0. The baker decides what to do with a half.
- A count ingredient has an optional "grams each". When filled in, its weight is taken out of the
  dough total, so portions keep the weight that was set. When blank, the ingredient is left out of
  the dough weight altogether.
- The weights table shows a count row's amount, with its weight when one is known, and no
  percentage.
- Saved recipes and the calculator remember each ingredient's kind, count and grams each. Existing
  recipes stay percentage ingredients and keep their weights.
- A saved ingredient remembers whether it is a count and its grams each, so picking "Eggs" sets the
  row up as a count. It holds no amount or percentage. Existing saved ingredients stay percentage
  ingredients.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `ingredient-weights`: ingredients can be counts; counts scale with portions; the flour calculation
  and weights table account for them.
- `dough-calculator`: count controls, choosing the kind and grams each while editing, and picking a
  saved count ingredient.
- `convert-recipe`: rows can be typed as counts with an optional grams each, and picking a saved
  ingredient sets the row's kind.
- `saved-ingredients`: a saved ingredient holds its kind and grams each.
- `saved-recipes`: a saved recipe holds each ingredient's kind, count and grams each.
- `full-recipe`: counts follow the temporary portions.

## Resolves

None.

## Out of Scope / Deferred

- F13 and F14 are unrelated and stay open.
- Unit labels for counts, such as "cloves": decided against, not to be revisited as a follow-up.
- Changing a saved ingredient's kind or grams each in place: delete it and tick it again when saving
  a recipe.

## Impact

- Recipe model and stored data: new optional ingredient fields, defaulted so existing data loads
  unchanged.
- Flour and weights calculation, and the weights table.
- Calculator ingredient card and ingredient editor; Convert screen and its conversion rules.
- Saved ingredients storage, saving, Undo and screen, plus the name dropdown's pick action.
- Full recipe screen follows through the shared recipe calculation and table.
- No new dependencies or permissions.
