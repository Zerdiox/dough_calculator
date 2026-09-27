---
id: F4
title: No undo after deleting a saved recipe
found: 2026-09-27
source: add-baseline-specs
capability: saved-recipes
location: app/src/main/java/com/example/pizza/dough/SavedRecipesScreen.kt:80
type: idea
size: S
---

## What
Deleting asks for confirmation, then the recipe is gone for good. The spec keeps the dialog and also
requires a snackbar naming the deleted recipe with an Undo action.

## Why it matters
A mistaken confirmation loses the recipe permanently.

## Notes
Undo must restore the recipe with the same values in its original place in the list.
