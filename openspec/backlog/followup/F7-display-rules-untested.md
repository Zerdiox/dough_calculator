---
id: F7
title: No tests for rounding, snapping and hidden rows
found: 2026-09-27
source: add-baseline-specs
capability: ingredient-weights
location: app/src/main/java/com/example/pizza/dough/DoughResultCard.kt:145
type: test-gap
size: S
---

## What
Unit tests cover only the weight math. The ingredient-weights rules for rounding (one decimal under
10 g, at most two decimals for percentages, half up), snapping to steps and hiding 0 g rows have no
tests.

## Why it matters
The coming per-recipe ingredients change will rework this area, and nothing would catch a regression
in how weights are shown.

## Notes
Best added before or as part of the ingredient change.
