# Design

## Context

`ConversionDraft.toRecipe()` in `RecipeConversion.kt` does two jobs today. It builds the recipe the
weights table shows, and its null result disables both buttons. The Convert screen shows
`DoughResultCard` only while it isn't null. Because it is strict (any blank name, field message,
blank Flour or small portions gives null), the table goes away whenever the buttons do. The
proposal and the `convert-recipe` delta spec say what should happen instead.

## Goals / Non-Goals

**Goals:**
- Separate what the table shows from whether the buttons are enabled.
- Leave the buttons' behaviour, and the recipe they save or send, exactly as it is now.

**Non-Goals:**
- No change to the field messages, the "Portions under 5 g" message, or when they show.
- No change to the calculator's or the full recipe's weights table.

## Decisions

### A lenient `preview()` and a strict `toRecipe()` built on top of it

`ConversionDraft` gains `preview(): DoughRecipe?`, which returns null only while the flour weight
can't be used. It keeps every row whose name has no error and whose grams make a usable percentage
above 0 g. Water counts as 0% unless it gives a usable percentage. The portion weight is Flour, Water
and the kept rows added up, divided by the portions and rounded down, with no 5 g minimum.

`toRecipe()` becomes `preview()` kept only while nothing blocks the conversion: every name valid,
no field message on Flour, Water or any row, and no "Portions under 5 g". When nothing blocks, the
preview already holds every row, the typed Water and the full total, so it is the same recipe
`toRecipe()` returns today.

- Alternative: a second, separate lenient function next to the current `toRecipe()`. Rejected: the
  two would repeat the percentage and total logic and could drift apart.
- Alternative: keep showing the last recipe that converted while the input is unfinished. Rejected:
  the table would show values that no longer match the fields.

The existing `RecipeConversionTest` cases for `toRecipe()` stay unchanged and guard that the strict
result didn't change.

### The screen reads both

`ConvertBody` shows `DoughResultCard` from `preview()`, and enables the buttons from `toRecipe()`
as now. While `preview()` is null it shows a small card in the table's place, with the same shape as
the result card, saying "Enter the flour weight to see the recipe". The save dialog and "Use in
calculator" keep using `toRecipe()`.

- Alternative: show nothing while Flour is blank, as today. Rejected: clearing Flour would still
  make the buttons jump, and the screen would give no hint of what to type first.

## Risks / Trade-offs

- [Water typed as "abc" shows 0% in the table] → its field says "Enter grams" and both buttons are
  disabled, so nothing wrong can be saved. Leaving out the water row isn't possible, since every
  recipe has water.
- [The table's portion weight leaves out a row with a blank name, while "Portions under 5 g" counts
  it] → the two only differ for a moment, while a name is being typed, and the buttons are disabled
  then anyway.
- [A portion weight below 5 g, even 0 g, reaches the weights table] → the table only multiplies by
  the portion weight and never divides by it, so 0 g shows as "4 × 0 g" with 0 g weights.
