# Design

## Context

Every ingredient is stored as a name and a percentage. The calculator, the full recipe and Convert's
preview all draw from one recipe model, one weights calculation and one table, so most behaviour can
change in one place. See proposal.md for motivation and the specs for behaviour.

## Goals / Non-Goals

**Goals:**
- One recipe calculation that handles both kinds of ingredient everywhere.
- Existing stored data loads without a migration step.

**Non-Goals:**
- Units or labels for counts.
- Editing a saved ingredient in place.

## Decisions

### Add an optional count to the stored ingredient
`Ingredient` gains `count: IngredientCount? = null`, and a non-null count makes it a count ingredient,
with its percentage left at 0%. A polymorphic ingredient type models the two kinds more strictly, but
would change the stored format and need an upgrade step. The optional field loads existing data as
it is, because a missing key takes the default.

### Store the amount with the portions it was set at
`IngredientCount` holds `halves: Int`, `forPortions: Int` and `gramsEachHundredths: Int?`. Scaling
always starts from the stored pair rather than the last rounded value, so moving from 4 to 3 and back
to 4 portions returns exactly 2. Portion changes need no extra code on the calculator or the full
recipe, because they already produce a recipe copy with a new portion count. A count per portion
alone can't hold 2 eggs for 3 portions exactly; a pair can.

### Whole-number arithmetic for the current amount
The current amount in halves is `(2 * halves * portions + forPortions) / (2 * forPortions)`, at least
1 when `halves > 0`. Integer division avoids floating-point surprises at the half-up boundary. It
lives on `DoughRecipe` beside `amounts()` so the table and the control share it.

### Rounded amounts drive the weights
A count's weight uses the amount the user sees, not the unrounded scale, so the shown count multiplied
by grams each always matches the shown weight and the table adds up to the total.

### Reuse `ValueStepper` through new rules
A `CountRules : ValueRules<Int>` works in halves, so the calculator gets typing, stepping and error
display from the existing control. The stepper derives its text from its value unless the user is
typing, so the field follows portion changes without extra state. Its `onValueChange` writes the new
halves with the recipe's current portions. Empty `unit` must leave no suffix; check this when
implementing it.

### Keep both kinds in the edit draft
`EditedIngredient` holds its percentage, its count and the grams each text alongside a kind, so
switching back within an edit restores what it held. `toIngredients` keeps only the chosen kind.
`ConvertedRow` gains a kind and a grams each text; the existing text stays as the amount, so switching
keeps what was typed, and validation depends on the kind.

### Grams each uses Convert's existing format
The Convert grams parser becomes shared so "Grams each" follows one rule on both screens: up to six
whole digits and two decimals, a comma counting as a point. Hundredths are stored as an integer,
matching `Percentage`.

### Clamp when counts outweigh the dough
The remainder for flour is clamped at 0, and the table shows a message. Blocking the portion or
count controls instead would trap the user: they need to step through such values on the way to the
amount they want.

### Replace saved names with a saved ingredient type
The stored list of names becomes a list of `SavedIngredient(name, isCount, gramsEachHundredths)`,
with a format upgrade turning each stored name into a percentage saved ingredient. The app already
upgrades stored data by version, and one list can't drift out of step the way a list of names beside
a lookup of counts could. Screens and the repository pass the whole saved ingredient, so Undo restores
its setup. The name dropdown keeps showing names; picking one looks up its setup.

## Risks / Trade-offs

- [Rows on Convert and the editor get taller] → The kind choice and grams each sit on their own line
  below the row, and grams each only shows for counts.
- [Older app versions would read a count ingredient as 0%] → Personal app with no downgrade path;
  accepted.
- [Scaling back up from a small batch after editing there multiplies the amount] → This is the spec'd
  "set for the current portions" behaviour, and the field shows the result at once.
