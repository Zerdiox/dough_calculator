---
id: F16
title: Flour tick, so a recipe's flour can be split across several ingredients
found: 2026-10-31
source: conversation
capability: ingredient-weights
location: app/src/main/java/com/example/doughcalculator/dough/DoughRecipe.kt (amounts)
type: idea
size: L
---

## What
Let an ingredient be ticked as flour, so a recipe can hold Bread flour 80% and Semolina 20% instead
of one fixed Flour row at 100%.

## Why it matters
Doughs that mix flours can't be expressed today, except in the note.

## Notes
Kept out of liquid-ingredients because it changes the calculation, whereas Liquid only changes a
displayed total. The main open question is what happens when the flours don't add up to 100%, such as
while adjusting one of them. It also affects Convert, where the base for every percentage becomes the
sum of several rows, and the reserved "Flour" name. The layout could mirror Hydration: `Flour 100%` at
the top with the flours under it. Flour would be a fourth choice beside `%`, Liquid and Count.
