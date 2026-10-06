---
id: F13
title: Recipe notes, e.g. to remember a flour blend
found: 2026-10-06
source: conversation
capability: saved-recipes
location: app/src/main/java/com/example/doughcalculator/dough/DoughRepository.kt (SavedRecipe)
type: idea
size: M
---

## What
A saved recipe can't hold free text, so there's nowhere to write down things like "400 g tipo 00 +
100 g whole wheat". The app has a single Flour.

## Why it matters
Converted recipes that use several flours lose the split. The user has to remember it themselves.

## Notes
Came up while planning `convert-recipe-from-grams`, which keeps a single flour on purpose. Adding a
note means a new stored field (with a default for existing data), a field when saving, and a place
to show it, probably on the full recipe.
