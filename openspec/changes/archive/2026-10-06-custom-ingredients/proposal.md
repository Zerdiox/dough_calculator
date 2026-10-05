# Proposal

## Why

The calculator only knows salt, yeast and olive oil, each with a fixed range and step. A dough that
needs honey, malt or a second salt addition can't be written down, and values outside the ranges
(90%+ hydration, more than 5% salt) can't be set. Letting each recipe have its own ingredients needs
percentage controls that work for any ingredient. A slider can't do that across 0 to 100% and still
hit 0.2% yeast.

## What Changes

- **Custom ingredients per recipe.** Flour stays fixed at 100% and water stays fixed as
  "Hydration". Every other ingredient is a named entry in the recipe, in the user's order. A new
  recipe starts with water only.
- **Typed percentages.** Every percentage, hydration included, is typed into a field from 0% to
  100%, with − and + buttons. The decimals typed (at most two) set the step: `60` steps by 1,
  `0.7` by 0.1, `0.25` by 0.01. The step is remembered with the ingredient and shown under the
  field ("± 0.1"). The field always shows that many decimals. **BREAKING:** the ingredient sliders
  and the fixed per-ingredient ranges are removed.
- **Portion weight.** Portion weight is typed into a field with − and + buttons in steps of 5 g,
  from 5 g with no practical upper limit. **BREAKING:** the 100–500 g slider is removed.
- **Edit mode for ingredients.** An Edit button on the Ingredients card lets the user rename,
  reorder by dragging, remove (with Undo) and add ingredients. Done applies the changes.
  Ingredient names can't be blank or used twice in a recipe.
- **Saved ingredients.** A list of ingredient names, shared by all recipes, is offered in a
  dropdown when adding an ingredient. A name joins the list only when the user ticks it in the
  save dialog. The list starts with Salt, Yeast and Olive oil. It has its own screen, reached from
  the saved recipes, where names can be deleted (with Undo). Recipes keep their own copies, so
  deleting a saved name changes no recipe.
- **Existing data is upgraded.** Salt, yeast and olive oil in saved recipes and on the calculator
  become ingredient entries; entries at 0% are dropped. Every recipe gives the same weights as
  before.
- **One value control.** Portion weight, hydration and every ingredient share one control
  component and one set of value rules, so a change to either is made in one place.
- **Tests for display rules.** The weights table's rounding, the snapping of values to their step,
  and the hiding of 0 g rows get tests (F7).

## Capabilities

### New Capabilities
- `saved-ingredients`: the shared list of ingredient names: what it holds, how it starts, how a
  name joins it, its screen and deleting from it.

### Modified Capabilities
- `ingredient-weights`: the fixed ingredient table becomes flour, water and a recipe's own
  ingredients; percentage precision and steps follow the typed decimals; ingredient names; the
  weights table lists the recipe's ingredients in order.
- `dough-calculator`: typed fields replace the sliders for portion weight and percentages; editing
  the ingredient list; new defaults; the save dialogs offer to add new ingredient names to the
  saved ingredients; the calculator remembers its ingredients.
- `saved-recipes`: a saved recipe holds its own ingredient list; loading one replaces the
  calculator's ingredients; recipes saved before this change keep their weights after the upgrade.

`full-recipe` is unchanged at spec level: its portion weight follows "the same limits and steps as
on the calculator" and picks up the new field through the shared control.

## Impact

- Recipe model and stored data: a new data format version with the first format upgrade, and a
  saved ingredient list in the stored data.
- Calculator: Portions and Ingredients cards, edit mode, save dialogs.
- Full recipe screen: the Portions card's new portion weight field.
- Saved recipes screen: a way to open the new saved ingredients screen, plus the new screen and its
  navigation entry.
- Weights table: rows come from the recipe's ingredient list.
- Dependency: a drag-to-reorder library for Compose (see design).
- Sequencing: `undo-delete-recipe` also changes the saved recipes screen and should be applied
  first; the saved ingredients screen reuses its message-with-Undo pattern.

## Out of Scope / Deferred

- Ingredients that contain flour or water (sourdough starter, preferments). They change how flour
  and hydration are worked out and need their own design.
- Renaming saved ingredients on their screen. Rename in a recipe and tick the new name when saving.
- Remembering a percentage with a saved ingredient. Saved ingredients are names only, by choice.
- Reordering the saved ingredients list. It is alphabetical.
- Renaming the app and package away from "pizza" (F9).
- Updating the project context in the OpenSpec config to describe custom ingredients, once this
  change is archived (F11).

## Resolves

- **F7**: no tests for rounding, snapping and hidden rows.
