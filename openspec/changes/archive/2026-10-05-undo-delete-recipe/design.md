# Design

## Context

See proposal.md for why. Deleting goes from the saved recipes screen, through the confirmation
dialog, to the ViewModel, which writes the delete in the application-wide scope, so it finishes
even if the screen closes. The screen has no message area yet.

In Material 3 1.4.0, `SnackbarHostState.showSnackbar` with an action label defaults to
`SnackbarDuration.Indefinite`, and messages wait for each other behind a mutex: a second message
appears only after the first one is gone.

## Goals / Non-Goals

**Goals:**
- A delete that is undone leaves the stored data exactly as before the delete.

**Non-Goals:**
- Undo across screens or app restarts.

## Decisions

### The delete is written at once; Undo writes the recipe back
Confirming writes the delete as today. Undo calls a new repository function that inserts the
recipe at the position it had, with the same id, name and values. If a recipe with that id is
already there, it changes nothing, so a repeated Undo can't create a copy. A position past the end
of the list puts it last.

- *Alternative: hide the recipe and write the delete only when the message ends.* A delete pending
  in memory is lost or half-done when the app closes in those ten seconds, the kind of lost write
  the app was just changed to avoid.

Keeping the id means the calculator's link to a loaded recipe works again after Undo, with no extra
work: the link is checked only when saving.

### The position is taken from the list on screen when Delete is confirmed
Nothing else changes the list while the message shows, and a second delete ends the first Undo, so
the index from the moment of confirming is the recipe's place when Undo is tapped.

### The message lives in the saved recipes screen
The screen gets a `SnackbarHost`, a coroutine scope from `rememberCoroutineScope`, and a new
`onRestoreRecipe(savedRecipe, index)` callback. After confirming, it dismisses any current message,
shows "Deleted "<name>"" with "Undo" and `SnackbarDuration.Long`, and calls `onRestoreRecipe` if
the result is `ActionPerformed`. Leaving the screen cancels its scope, which ends the message and
the chance to undo.

- *Alternative: keep the message in `PizzaApp` across screens.* It would need a message area
  shared by screens for a case the proposal leaves out.
- *Explicit duration:* without it, a message with an action never goes away by itself.
- *Dismiss first:* without it, a second delete's message waits behind the first for up to ten
  seconds.

## Risks / Trade-offs

- [The user taps Undo just as the message times out] → Either it counts or it doesn't; the list
  then shows plainly whether the recipe is back.
- [Accessibility services can lengthen `SnackbarDuration.Long`] → Fine; Undo stays offered longer.
