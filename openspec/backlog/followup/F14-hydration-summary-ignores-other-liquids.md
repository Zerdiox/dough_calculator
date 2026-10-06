---
id: F14
title: Saved-recipes summary understates hydration when another liquid replaces water
found: 2026-10-06
source: conversation
capability: saved-recipes
location: app/src/main/java/com/example/doughcalculator/dough/SavedRecipesScreen.kt (summary)
type: idea
size: S
---

## What
The saved-recipes summary only counts water. A recipe with Beer 65% and Water 0% reads "0%
hydration".

## Why it matters
The summary is misleading for beer or milk doughs. The weights themselves are correct.

## Notes
We decided not to add a "counts as liquid" setting for now. Possible fixes: mark ingredients as
liquid, or reword the summary.
