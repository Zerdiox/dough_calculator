# Proposal

## Why

Deleting a saved recipe asks for confirmation, but once confirmed the recipe is gone for good. One
mistaken tap on Delete loses a recipe that can't be brought back. The spec already asks for a
message with Undo after deleting; the app doesn't show one.

## What Changes

- After a recipe is deleted, the saved recipes list shows "Deleted "<name>"" with an Undo action
  for about ten seconds.
- Undo puts the recipe back unchanged, in its old place in the list. If the calculator was loaded
  from it, saving offers to update it again.
- Undo is offered only while the list is open. Deleting another recipe while the message shows
  replaces the message; the first delete stays done.
- The confirmation dialog stays as it is.
- The delete is still written at once; Undo writes the recipe back. No new permissions or
  dependencies, and the stored format doesn't change.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `saved-recipes`: "Delete a saved recipe" gains the message text, how long Undo is offered, a
  second delete replacing the message, and the loaded recipe working again after Undo.

## Impact

- The saved recipes screen: a message area and the Undo action.
- Recipe writes: a way to put a deleted recipe back at a given place.

## Out of Scope / Deferred

- Undo after leaving the saved recipes list, or after closing the app.
- Undoing more than the most recent delete.
- Dropping the confirmation dialog. Kept on purpose: deleting is rare, and a recipe is worth the
  extra tap.

## Resolves

- **F4**: no undo after deleting a saved recipe.
