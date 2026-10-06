# Tasks

## 1. Describe the current app

- [x] 1.1 Rewrite the intro so it says the app works out ingredient weights for a batch of dough from
  flour, water and whichever other ingredients a recipe names, without listing salt, yeast and oil
  or saying "pizza". Verify the intro names no fixed ingredient set.
- [x] 1.2 Change the formula so it sums water and every other ingredient instead of
  `hydration + salt + yeast + oil`, and check it against the flour formula in the ingredient-weights
  spec. Verify the two say the same thing.
- [x] 1.3 Rewrite "What it does" as one bullet per feature, named by what it is for: calculator
  (portions, portion weight, each recipe's own ingredients typed as percentages), saved recipes,
  saved ingredients (new) and full recipe. Leave out exact limits, ranges and steps, and don't
  mention sliders. Verify that searching the README for "slider", "pizza count", "ball weight" and
  any number followed by "%" or "g" in that section finds nothing.
- [x] 1.4 In "Stack", change "the three screens" to four. Verify against the app's navigation that
  there are four screens: calculator, saved recipes, saved ingredients and full recipe.

- [x] 1.5 In the storage paragraph, say that the saved ingredients are written to `dough.json`
  along with the current and saved recipes. Verify against the stored data that they are.

## 2. Check and build

- [x] 2.1 Read the whole README top to bottom and check every remaining claim against the app and
  build (storage and backup, permissions, Building steps, minSdk, release signing, badges). Note any
  that are wrong as follow-up candidates rather than fixing them here, unless they are on the lines
  this change already rewrites.
- [x] 2.2 Run `./gradlew check` and verify it is green.

## 3. Follow-up harvest

- [x] 3.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
