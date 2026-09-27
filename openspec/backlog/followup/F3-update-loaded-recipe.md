---
id: F3
title: Saving after loading a recipe always creates a new one
found: 2026-09-27
source: add-baseline-specs
capability: dough-calculator
location: app/src/main/java/com/example/pizza/PizzaApp.kt:61
type: idea
size: M
---

## What
"Edit in calculator" only copies a saved recipe's values into the calculator. Saving afterwards
always adds a new recipe. The spec requires offering "update the loaded recipe" (keeping its name) or
"save as new".

## Why it matters
Without it, the only way to change a saved recipe is to save a copy and delete the original.

## Notes
The calculator needs to remember which recipe it was loaded from. If that recipe is deleted
afterwards, saving offers only "save as new".
