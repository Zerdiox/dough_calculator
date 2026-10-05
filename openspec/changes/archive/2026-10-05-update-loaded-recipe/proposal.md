# Proposal

## Why

"Edit in calculator" copies a saved recipe's values into the calculator, but saving afterwards
always adds a new recipe. The only way to change a saved recipe is to save a copy and delete the
original. The spec already asks for an "update the loaded recipe" choice; the app doesn't offer it.

The calculator can also forget its last change: values are written a moment after the last
adjustment, and leaving the app in that moment drops the write. Once saving can update a recipe,
that matters more, because Update must save what the screen shows.

## What Changes

- The calculator remembers which saved recipe its values were loaded from, also after the app is
  closed and opened again.
- When that recipe still exists, the save button first offers a choice: update that recipe with
  the calculator's current values, keeping its name, or save as new. "Save as new" opens the same
  name dialog as today. Updating confirms with a short message naming the recipe.
- After saving as new, the calculator counts as loaded from the new recipe, so the next save
  offers to update that one.
- When the loaded recipe has been deleted, or nothing was loaded, saving works exactly as today.
- Loading a recipe stores its values and the link together at once, so the app can never link a
  recipe to values that aren't its own.
- A change made just before leaving the app is still written, instead of being dropped.
- No new screens, no label on the calculator, no new permissions or dependencies.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `dough-calculator`: "Update a loaded recipe" gains the confirmation message, the link surviving
  a restart, and the link moving to a recipe saved as new.

"Values survive a restart" already requires that a last change isn't lost, so F6 needs no spec
change; the app just doesn't meet it yet.

## Impact

- Stored data gains the id of the loaded recipe. It isn't written while nothing is loaded, older
  data loads unchanged, and the format version stays the same.
- The calculator screen: a new choice dialog before the existing save dialog, and a new message.
- Loading, saving and updating recipes, and how calculator values are written when the screen
  closes.

## Out of Scope / Deferred

- **F4** (undo after deleting a recipe). The link points at a recipe by its id and is checked only
  when saving, so a later undo makes Update available again without extra work here.
- Showing on the calculator which recipe is loaded. The screen stays as it is.
- A way to drop the link by hand. "Save as new" is always offered next to Update.

## Resolves

- **F3**: saving after loading a recipe always creates a new one.
- **F6**: the last calculator change can be lost when leaving the app.
