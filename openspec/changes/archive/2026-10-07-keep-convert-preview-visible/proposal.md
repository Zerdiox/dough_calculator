# Proposal

## Why

On the Convert screen the weights table disappears whenever any input can't be used yet. Tapping
"Add ingredient" adds a row with a blank name, so the table vanishes until the name is typed, and
everything below it jumps up and back down. The table should keep showing while a recipe is being
typed in.

## What Changes

- The weights table on Convert shows the recipe made from the input that can be used so far. A row
  is left out of the table while its name or grams can't be used yet: blank, breaking the name
  rules, saying "Enter grams", or over 200%. The other rows stay in the table.
- Water that can't be used yet counts as 0 g in the table, the same as blank Water today.
- While the flour weight can't be used (blank, 0, or not a weight), the table's place shows "Enter
  the flour weight to see the recipe" instead of the table.
- With "Portions under 5 g", the table still shows, with the small portion weight.
- "Save as recipe" and "Use in calculator" follow the same rules as today. They stay disabled while
  any field shows a message, a name is blank, Flour is blank, or the portions are under 5 g. A
  named row without grams is still left out and doesn't disable them.

Assumptions made while planning, open to review:
- The portion weight in the table comes from the weights shown in it, so a row with a blank name
  doesn't count towards the total until it has a name.
- The "Enter the flour weight to see the recipe" message sits where the table would be, in the same
  card style, so the buttons below don't move much when Flour is cleared.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `convert-recipe`: "Converted recipe preview" keeps the weights table on screen while input is
  unfinished, leaving out rows that can't be used, and shows a message in its place while Flour
  can't be used. The buttons' rules don't change.

## Out of Scope / Deferred

- The keyboard covering the "Removed … / Undo" message after removing a row, noted while building
  `convert-recipe-from-grams`. It is a candidate follow-up, not part of this change.
- No open follow-up matches `convert-recipe`. F13 (recipe notes) and F14 (hydration summary) belong
  to other capabilities.

## Resolves

None.

## Impact

- Conversion logic: a lenient preview next to the strict recipe the buttons use. The strict recipe
  and its tests stay as they are.
- Convert screen: shows the preview instead of the strict recipe, and the flour message when there
  is no preview.
- Unit tests for the preview, and a UI test for "Add ingredient" and blank Flour.
- No new dependencies, data changes or permissions.
