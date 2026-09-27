---
id: F6
title: Last calculator change can be lost when leaving the app
found: 2026-09-27
source: add-baseline-specs
capability: dough-calculator
location: app/src/main/java/com/example/pizza/dough/DoughViewModel.kt:37
type: bug
size: S
---

## What
Calculator values are written 300 ms after the last change. Leaving the app within that window (e.g.
moving a slider, then pressing Back) cancels the pending write, so the change is not remembered.

## Why it matters
Breaks the "Remembered values" requirement: the calculator can reopen with older values.

## Notes
The delay exists so dragging a slider doesn't write to storage on every frame. The pending write
must still finish when the screen closes.
