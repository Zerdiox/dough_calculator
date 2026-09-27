# Proposal

## Why

The app has no specs yet, so every future change starts without a written statement of how the app
should behave. A change to the ingredient set is coming soon; it needs a baseline to change against,
and the behaviour decided while exploring (portions wording, real recipe editing, undo on delete,
recipes surviving updates) needs to be written down before it is lost.

## What Changes

- Add four specs describing how the app should behave today, including the decisions made while
  exploring. No app code changes in this change.
- Where the app does not yet match these specs, the differences are listed below as follow-up
  candidates rather than fixed here.

## Capabilities

### New Capabilities
- `ingredient-weights`: the ingredient list with each ingredient's range, step and default
  percentage, how weights are worked out from baker's percentages, and how the weights table is
  shown. Kept separate because the ingredient set will change soon.
- `dough-calculator`: setting portions, portion weight and ingredient percentages, the defaults,
  remembering values across restarts, and saving the calculator as a recipe (including updating a
  loaded recipe).
- `saved-recipes`: what a saved recipe holds, browsing them, opening one, loading one into the
  calculator, deleting with confirmation and undo, and keeping recipes intact across app updates.
- `full-recipe`: following a recipe while making dough, with temporary changes to portions and
  portion weight, and the screen kept on.

### Modified Capabilities
None.

## Impact

Only `openspec/specs/` once archived. No code, build or data changes.

## Out of Scope / Deferred

- Adding and removing ingredients per recipe. The coming ingredient change will rewrite
  `ingredient-weights` and must migrate existing saved recipes.

Where the app differs from these specs, tracked as follow-ups:

- **F1**: the portion count can go down to 0; the specs require at least 1.
- **F2**: the app still says "Pizzas" and "Dough ball weight" instead of "Portions" and "Portion
  weight".
- **F3**: saving after loading a saved recipe always creates a new recipe; the specs require
  offering to update the loaded recipe or save as new.
- **F4**: deleting only asks for confirmation; the specs also require an Undo snackbar afterwards.
- **F5**: unreadable stored data silently resets all saved recipes; the specs require recipes to
  survive app updates. Matters most for the coming ingredient change, which must migrate saved
  recipes.

## Resolves

None.
