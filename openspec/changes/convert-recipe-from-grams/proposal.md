# Proposal

## Why

Most recipes in books and on websites list ingredients in grams, while the app only takes baker's
percentages, so trying a found recipe means working the percentages out by hand first. A Convert
screen that takes the grams and hands back a recipe in percentages removes that step. Wet doughs
also need water above 100% to be entered at all.

## What Changes

- A new **Convert** screen, opened from a button in the calculator's top bar. It takes a recipe in
  grams (Flour, Water and any named ingredients) plus a number of portions. It shows the resulting
  recipe in baker's percentages as a weights table. A removed ingredient row can be brought back
  with Undo, as when editing the calculator's ingredients.
- Each percentage is the ingredient's weight divided by the flour weight. It is rounded half up to
  two decimals, with a step that follows the decimals it needs. The portion weight is the total
  weight divided by the portions, rounded down to whole grams.
- **Use in calculator** replaces the calculator's values with the converted recipe, without asking.
  The calculator then counts as not loaded from any saved recipe.
- **Save as recipe** asks for a name with the existing save dialog, including "Add to saved
  ingredients", and saves a new recipe. It leaves the calculator untouched. With **Add more** ticked,
  the user stays on Convert with the weights cleared, ready for the next recipe. Without it, the new
  recipe's full recipe opens, and Back from there goes to the calculator.
- The upper limit for water and every ingredient goes from 100% to **200%**, everywhere in the app.
  The field message becomes "Enter 0 to 200".

Assumptions made while planning, open to review:
- Gram fields take decimals (up to two, a comma counts as a decimal point), since small amounts such
  as 1.5 g of yeast are common in written recipes.
- A named row with a blank or 0 g weight is left out of the result. A row with a blank or
  rule-breaking name disables both buttons, the same way it disables Done while editing.
- The Convert screen keeps its input while the phone is rotated, but not after the app is closed.

## Capabilities

### New Capabilities
- `convert-recipe`: turning a recipe written in grams into a recipe in baker's percentages, and
  sending it to the calculator or saving it as a recipe.

### Modified Capabilities
- `ingredient-weights`: "Ingredient list" raises the range for water and every ingredient to 0% to
  200%.
- `dough-calculator`: "Calculator screen" gains the Convert button. "Ingredient percentage controls"
  stops at 200% and says "Enter 0 to 200". "Edit the ingredients" makes Convert unavailable while
  editing.
- `saved-ingredients`: "Add names when saving a recipe" also covers saving from the Convert screen.
- `full-recipe`: "Full recipe screen" returns to the calculator on Back when it was opened by saving
  from the Convert screen.

## Out of Scope / Deferred

- Several flours, sourdough starters and preferments (poolish, biga): the user adds them up
  themselves. Volume units such as cups and spoons are also left out.
- New follow-up candidates, for Kenny to approve:
  - A note field on recipes, for example to remember a flour blend.
  - The saved-recipes summary understates hydration when a liquid such as beer is a named
    ingredient instead of water.
- No open follow-up matches these capabilities: F9 belongs to the app rename and F12 to the README.

## Resolves

None.

## Impact

- Starts after `rename-app-to-dough-calculator` is committed. Every path in this change uses the
  renamed `com.example.doughcalculator` package.
- Percentage model and rules: a separate upper limit of 200%, while the 100% that flour stands for
  stays as it is. Stored recipes are all at or below 100%, so they stay valid with no data upgrade.
- The repository gains two writes. One saves a recipe without linking the calculator to it. The
  other puts a recipe on the calculator and clears the link, in one write.
- New conversion logic with unit tests, a new Convert screen, and a new navigation entry. The
  existing save dialog, ingredient name rules, saved-ingredients dropdown, portions stepper and
  weights table are reused.
- No new dependencies or permissions.
