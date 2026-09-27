---
id: F5
title: Unreadable stored data silently resets all recipes
found: 2026-09-27
source: add-baseline-specs
capability: saved-recipes
location: app/src/main/java/com/example/pizza/dough/DoughRepository.kt:32
type: tech-debt
size: M
---

## What
If the stored data can't be read, the app starts over with defaults, and the saved recipes are
overwritten on the next save. The spec requires that saved recipes survive app updates with the same
weights.

## Why it matters
The coming per-recipe ingredients change alters what a saved recipe contains. A format change that
older data can't be read into would wipe every saved recipe without saying anything.

## Notes
Kenny decided the ingredient change is responsible for migrating existing recipes. Corrupted files
are rare enough to ignore. The real risk is a format change.
