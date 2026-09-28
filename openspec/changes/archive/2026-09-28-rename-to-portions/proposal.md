# Proposal

## Why

The specs call the count "Portions" and the weight "Portion weight", with a minimum of 1 portion,
and call the water percentage "hydration" in the saved recipes list. The app still says "Pizzas",
"Dough ball weight" and "62% water", and lets the portion count drop to 0. The code uses the same
pizza words as the old labels, so as long as the code and the specs disagree, every future change
has to translate between the two. The coming ingredient change will touch all of these places, so
the words should match first.

## What Changes

- The portion count can no longer go below 1: the − button is disabled at 1, on the calculator and
  on the full recipe.
- The portions card reads "Portions" and "Portion weight", and its − and + buttons are announced as
  "Fewer portions" and "More portions".
- A saved recipe's summary reads "4 × 250 g · 62% hydration" instead of "62% water".
- The code names for the portion count, the portion weight and the controls that set them use
  "portion" instead of "pizza" and "ball".
- Saved recipes and the remembered calculator values are stored exactly as before, so everything
  already saved still loads with the same values. No data migration.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
None. The specs already describe this behaviour (`dough-calculator` Portions and Portion weight,
`full-recipe` controls, `saved-recipes` list summary); this change brings the app in line with them.
The change sets `skip_specs: true`.

## Impact

- The portions card, its stepper and the saved recipes list summary.
- The recipe model, the calculator and full recipe screens, and the unit tests, where the portion
  names are used.
- The README's description of the app, which still says pizzas and dough balls.
- Stored data: unchanged format; a test pins it.
- No new dependencies.

## Out of Scope / Deferred

- The app, package, application ID and theme names keep "Pizza". They name the app, not the dough,
  and changing the application ID would install as a new app with empty storage. **New candidate
  follow-up** for Kenny to approve: "App, package and theme names still say Pizza".
- A portion count of 0 that is already stored is not corrected on load. It opens at 0 with − disabled
  and + working, and the next tap on + fixes it.
- Changing the stored field names to match the code. That is a data migration and belongs with the
  coming ingredient change, which already has to migrate saved recipes (**F5**).

## Resolves

- **F1**: portion count can go down to 0.
- **F2**: app still says "Pizzas" and "Dough ball weight".
- **F8**: saved recipe summary says "water" instead of "hydration".
