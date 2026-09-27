---
id: F2
title: App still says "Pizzas" and "Dough ball weight"
found: 2026-09-27
source: add-baseline-specs
capability: dough-calculator
location: app/src/main/java/com/example/pizza/dough/RecipeControls.kt:51
type: bug
size: S
---

## What
The specs call the count "Portions" and the weight "Portion weight". The app shows "Pizzas",
"Dough balls", "Dough ball weight" and "Fewer/More pizzas".

## Why it matters
The app is becoming dough-agnostic (the ingredient set is changing), and pizza-specific wording
contradicts the specs.

## Notes
Also affects the saved-recipes and full-recipe capabilities, since they show the same controls. The
save dialog placeholder "e.g. Friday pizza night" is only an example and can stay.
