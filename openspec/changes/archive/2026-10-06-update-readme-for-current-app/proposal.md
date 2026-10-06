# Proposal

## Why

The README is the only description of the app on GitHub, and its body still describes the pizza
calculator from before custom ingredients: a fixed set of salt, yeast and oil, sliders with fixed
ranges, three screens, and no saved ingredients. It should describe the app as it is now, in terms
that won't go stale with the next tweak to a limit or step.

## What Changes

- **Intro and formula.** The intro says the app works out ingredient weights for a batch of dough
  from flour, water and whichever other ingredients a recipe names, instead of listing flour, water,
  salt, yeast and oil for pizza dough. The formula sums water and every other ingredient instead of
  `hydration + salt + yeast + oil`.
- **What it does.** Each feature is named by what it is for: the calculator (portions, portion
  weight, and each recipe's own ingredients typed as percentages), saved recipes, saved ingredients
  (new bullet: a reusable list of ingredient names) and the full recipe screen. No exact limits,
  ranges or steps, so the README doesn't drift when those change. The removed sliders are no longer
  mentioned.
- **Stack.** "the three screens" becomes four.
- **Storage.** The paragraph on `dough.json` names the saved ingredients alongside the recipes.
- The rest of the README (backup, Building, release signing, badges) is still accurate
  and stays as is.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None. Only the README changes; the app's behaviour does not, so this change opts out of specs.

## Impact

- `README.md` only. No code, resources or build configuration.

## Out of Scope / Deferred

- Renaming the app, package and theme away from "Pizza", and the "Dough Calc" launcher name (F9,
  `rename-app-to-dough-calculator`). That change adds the launcher name to the README when it lands.

## Resolves

- **F12**: README still describes the old pizza calculator.
