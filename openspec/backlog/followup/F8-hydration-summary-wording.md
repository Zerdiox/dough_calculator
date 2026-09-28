---
id: F8
title: Saved recipe summary says "water" instead of "hydration"
found: 2026-09-28
source: add-baseline-specs
capability: saved-recipes
location: app/src/main/java/com/example/pizza/dough/SavedRecipesScreen.kt:157
type: bug
size: S
---

## What
The saved recipes list shows "4 × 250 g · 62% water". The water percentage should be called
"hydration" wherever the app labels it as a percentage: "4 × 250 g · 62% hydration".

## Why it matters
The calculator already labels the control "Hydration", so the list uses a different word for the
same number.

## Notes
Only the percentage is renamed. The ingredient you weigh stays "Water", including its row in the
weights table ("Water  62%  375 g"). The calculator's "Hydration" label already matches.
