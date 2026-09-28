# Design

## Context

See proposal.md for why. The count and weight are properties of the recipe model, which is stored
as JSON: the remembered calculator values and every saved recipe use the property names as JSON
keys. The stored data is read with unknown keys ignored, and the count and weight have no defaults,
so a renamed key would not be found, reading would fail, and the app would start over with defaults.
That wipes every saved recipe without saying anything, which is what F5 describes.

The portions card, with its stepper and weight slider, is shared by the calculator and the full
recipe, so fixing it once covers both screens.

## Goals / Non-Goals

**Goals:**
- Code names for the portion count and portion weight match the specs' words.
- Data that is already stored reads back with the same values, proven by a test.

**Non-Goals:**
- Changing the stored format or its key names.
- Renaming anything that names the app rather than the dough (package, application ID, app and
  theme names).

## Decisions

### Keep the stored keys with `@SerialName`
The renamed properties keep their old JSON keys through `@SerialName("pizzaCount")` and
`@SerialName("ballWeightGrams")`, with a one-line comment saying the keys are kept so stored
recipes still load.

- *Alternative: new keys, reading old ones through `@JsonNames`.* Old files would still load, but
  the next save rewrites them in a format an older build can't read. That's a format migration,
  which the ingredient change owns (F5). It adds risk for no user-visible gain.
- *Alternative: rename without annotations.* This silently resets all stored data, as described
  above.

### Where the rename stops
Rename everything that means "the portion count", "the portion weight" or "the controls that set
them": the two model properties, the card, the stepper, the maximum count constant, the weight
slider's scale, the full recipe's local state, the summary text and the unit test names. Keep the
package, the application ID, the app composable and the theme names.

### The minimum is a constant next to the maximum
The stepper gets a minimum of 1 alongside the existing maximum of 50, and − is enabled only above
the minimum. The weight maths stays correct at 0 portions (0 g), so the existing zero-portion unit
test stays, renamed, as a check on the maths.

### The card's subtitle
"Dough balls" is pizza wording, and the specs don't name a subtitle. It becomes "How many, and how
heavy", which describes both controls in the card. The card keeps its current layout.

### Tests
- **Stored format (unit test):** decode a JSON string in today's format and check the portion count
  and portion weight, then encode a recipe and check it still writes the old keys. This runs in
  `./gradlew check`.
- **Stepper limit (Compose UI test, instrumented):** at 1 portion, "Fewer portions" is disabled; at
  2 it is enabled. This needs an emulator or device (`connectedDebugAndroidTest`) and is not part of
  `./gradlew check`, so the manual checklist covers it as well.

## Risks / Trade-offs

- [The JSON keys no longer match the property names] → `@SerialName` sits on the property with a
  comment, and the format test fails if someone removes it.
- [A search-and-replace catches the wrong "pizza" (package, theme, app ID)] → Rename with the IDE's
  rename refactoring, one symbol at a time, following the list above. `./gradlew check` and the
  format test confirm the result.
- [A stored count of 0 still opens at 0] → Accepted. See proposal.md, Out of Scope.
