# Proposal

## Why

The app is called Dough Calculator in the README, the project context and the GitHub repository,
but on the phone it is still "Pizza", and its package, application class, root composable and theme
are named after pizza too. The dough wording already moved to "portions", so the remaining pizza
names are the last place where the app and its name disagree.

## What Changes

- The name under the launcher icon and in the system's app settings becomes "Dough Calc", short
  enough to fit under the icon without being cut off.
- The Gradle project is named "Dough Calculator".
- The Kotlin package and the build namespace move from `com.example.pizza` to
  `com.example.doughcalculator`, in the app code and in both test source sets.
- The application class, the root composable and the theme (Compose and XML) take "DoughCalculator"
  in place of "Pizza".
- The application ID moves from `com.example.pizza` to `com.example.doughcalculator` as well, so
  Dough Calc installs as a new app next to the old Pizza app and starts with empty storage. Saved
  recipes, saved ingredients and calculator values in the old app are not carried over; the old app
  is uninstalled by hand.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
None. No spec names the app or describes its launcher label; the calculator, full recipe, saved
recipes and saved ingredients behave exactly as before. The change sets `skip_specs: true`.

## Impact

- The app name string, the Gradle settings and the app's build file.
- Every Kotlin file in the app and its tests, through the package move.
- The manifest and the XML theme, through the class and theme renames.
- Stored data: same file and format, but in the new app's own storage; the new app starts empty.
- No new dependencies.

## Out of Scope / Deferred

- Moving data from the old app to the new one. Kenny chose to start fresh rather than build an
  export and import for it.
- Stored field names such as `pizzaCount` keep their spelling: they are the stored format, and
  changing them is a format change, not a rename.
- The "Friday pizza night" sample recipe name in placeholders and previews, and the "Pizza palette"
  comment on the colours: they describe dough, not the app's name.
- The project folder on disk stays `Pizza`; moving it is outside the repository.
- The README still describes the old fixed-ingredient pizza calculator (**F12**).

## Resolves

- **F9**: app, package and theme names still say Pizza.
