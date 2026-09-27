---
id: F1
title: Portion count can go down to 0
found: 2026-09-27
source: add-baseline-specs
capability: dough-calculator
location: app/src/main/java/com/example/pizza/dough/RecipeControls.kt:82
type: bug
size: S
---

## What
The − button on the portion count stays enabled down to 0. The spec says the minimum is 1 portion.

## Why it matters
At 0 the weights table is empty apart from "TOTAL DOUGH 0 g", which looks broken.

## Notes
The same stepper is used on the full recipe screen, so one fix covers both.
