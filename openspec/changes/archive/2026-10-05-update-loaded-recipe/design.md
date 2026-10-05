# Design

## Context

See proposal.md for why. The app stores one JSON file through a single DataStore that the
Application class owns: the calculator's recipe, the saved recipes and the reset-message flag.
Saved recipes already have an id; names may repeat, so only the id identifies a recipe.

The ViewModel keeps the calculator's values in memory as they change and writes them 300 ms after
the last change, so dragging a slider doesn't write on every frame. "Edit in calculator" goes
through the same delayed path today. Every write is launched in `viewModelScope`.

Lifecycle 2.11.0 closes `viewModelScope` before it calls `onCleared`, so a write still waiting in
that scope when the screen closes is cancelled. That is F6. In a plain JVM test the ViewModel can be
built, but anything launched in `viewModelScope` fails, because Android's Main dispatcher isn't
there. Work launched in another scope runs normally.

## Goals / Non-Goals

**Goals:**
- The stored data can never link a recipe to values that aren't its own.
- A write the user started always reaches the disk, even if the screen closes right after.

**Non-Goals:**
- Surviving the process being killed in the 300 ms after a slider change. Leaving the app doesn't
  kill the process, and that is the case F6 is about.

## Decisions

### The link is an optional id in the stored data
`DoughData` gains `loadedRecipeId: String? = null`. The ViewModel derives the loaded recipe by
finding that id in the saved recipes and exposes it as a `StateFlow<SavedRecipe?>`. A deleted
recipe isn't found, so the calculator falls back to plain saving without any cleanup on delete,
and a future undo of the delete makes the link work again.

- *Alternative: store a copy of the recipe's name with the link.* It would go stale when the
  recipe changes, and names aren't unique.
- *Alternative: clear the link when the recipe is deleted.* An extra write in the delete path that
  an undo would then have to restore.

### The format version stays 1
A property that holds its default isn't written, so files without a loaded recipe don't change,
and a file without the key decodes to `null`. Older builds ignore the unknown key. Nothing about
existing data needs upgrading, so no upgrade step is added. Upgrade steps are for data that
wouldn't decode or would decode wrongly in the new shape; an optional field with a default does
neither.

### Loading writes the values and the link in one update, at once
A new repository function sets `currentRecipe` and `loadedRecipeId` in a single `updateData`.
Loading is one tap, not a drag, so it doesn't use the 300 ms delay. Before writing, the ViewModel
cancels any pending delayed save and puts the loaded values in memory, so the screen shows them at
once.

Cancelling matters: a slider save still waiting from just before the load would otherwise land
after it and replace the loaded values, leaving the link on values that aren't the recipe's. Then
Update would silently overwrite the recipe with them.

- *Alternative: write the link on its own, right away, and let the values follow through the
  delayed path.* Closing the app in between leaves the link on the previous values, the same
  damage from the other side.

### Saving as new moves the link in the same update
The new recipe's id is created before `updateData`, because the transform may run more than once.
The same update adds the recipe and sets `loadedRecipeId` to it.

### Updating replaces the values in place
A new repository function replaces the recipe values of the saved recipe with the given id,
keeping its id, name and place in the list. If no recipe has that id, it changes nothing. Update
saves the values the calculator shows.

### Writes run in an application-wide scope
`PizzaApplication` owns one `CoroutineScope` (a `SupervisorJob` on `Dispatchers.IO`). It is passed
to the DataStore, which today creates its own, and to the ViewModel through the factory. The
ViewModel launches every write in it: the delayed calculator save, load, save as new, update,
delete and dismissing the reset message. A pending delayed save then finishes after the screen
closes; a new change still cancels and replaces it as today. Reading stays in `viewModelScope`.

- *Alternative: write the pending values from `onCleared`.* It needs the same outliving scope
  anyway, adds a second write path, and only saves a few hundred milliseconds.
- *Alternative: run blocking in `onCleared`.* It runs on the main thread.
- *Alternative: only the delayed save in the outliving scope.* Save and delete also run in
  `viewModelScope` and could be cut off the same way when the user leaves at once; one rule for
  every write is simpler.

### The choice comes before the existing name dialog
When the loaded recipe exists, the save button opens a dialog titled "Save recipe" with two
choices, *Update "<name>"* and *Save as new…*, plus Cancel. *Save as new…* opens today's name dialog
unchanged; *Update* updates and shows "Updated "<name>"". Without a loaded recipe the save button
opens the name dialog directly. Back and taps outside the choice dialog cancel it, like the name
dialog; only the reset message ignores them.

The calculator content takes the loaded recipe's name (or `null`) and an update callback, so the
dialog logic stays testable in a Compose UI test without a ViewModel.

## Risks / Trade-offs

- [The process is killed within 300 ms of a slider change] → The change is lost. Accepted: the
  system doesn't kill a just-left app that fast, and the old values plus the link still agree.
- [Update saves values a few hundred milliseconds newer than what's on disk for the calculator] →
  Harmless. Update uses what the screen shows, and the delayed save brings the disk up to date.
- [`viewModelScope` can't run in a JVM test] → The ViewModel's writes run in the given scope, so
  tests pass a test scope and cover them on the JVM. Finding the loaded recipe is a pure function
  of the stored data and is tested without the ViewModel; the reads in `viewModelScope` are
  covered by the manual checklist.
- [A write in the application scope fails] → It fails as it does in `viewModelScope` today; the
  scope's `SupervisorJob` keeps other writes going.

## Migration Plan

Nothing to migrate. Existing files load with no loaded recipe, so the first save after the update
behaves as today. Rolling back to the previous build works, because it ignores the unknown key.
