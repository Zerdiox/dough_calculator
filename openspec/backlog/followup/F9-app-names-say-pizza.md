---
id: F9
title: App, package and theme names still say Pizza
found: 2026-09-28
source: rename-to-portions
capability:
location: app/build.gradle.kts:23
type: tech-debt
size: M
---

## What
The package and application ID are `com.example.pizza`, and the app composable and theme are
`PizzaApp`, `PizzaTheme`, `PizzaColorScheme` and `PizzaShapes`. The dough wording moved to
"portions", but these names still name the app after pizza.

## Why it matters
Only matters if the app gets a new name. Until then the names are consistent with the app, just not
with its dough-agnostic direction.

## Notes
Changing the application ID installs as a new app with empty storage, so saved recipes would be left
behind in the old install. Renaming the package and theme names alone is safe. Kept out of
rename-to-portions on purpose.
