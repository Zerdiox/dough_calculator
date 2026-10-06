# Design

## Context

See proposal.md for why. Android keeps two names apart that the project template set to the same
value: the application ID, which identifies the installed app and owns its storage and backups, and
the build namespace, which is the package the generated `R` class lives in and the base that the
manifest's relative class names (`.MainActivity`) resolve against. The Kotlin package of the source
files is a third, independent name, which by convention matches the namespace.

## Goals / Non-Goals

**Goals:**
- No "Pizza" left in the launcher label, the project name, the application ID or any Kotlin package
  or class name that names the app.

**Non-Goals:**
- Changing anything a user does in the app.
- Carrying data over from the old install.
- Touching words that describe dough rather than name the app (sample recipe names, stored keys).

## Decisions

### One name everywhere: `com.example.doughcalculator`
The application ID, the namespace and the Kotlin package all become `com.example.doughcalculator`,
the template's convention of the app name in lower case without spaces. The existing `dough` and
`ui.theme` subpackages keep their names under it.

- *Alternative: keep the application ID `com.example.pizza`.* The update would install over the old
  app and keep its data, at the cost of one lasting "pizza" in the build file. Starting with an empty
  app is acceptable here, so the names stay consistent instead.

### Class and theme names use "DoughCalculator"
`DoughCalculatorApplication`, `DoughCalculatorApp`, `DoughCalculatorTheme`,
`DoughCalculatorColorScheme`, `DoughCalculatorShapes` and the XML style `Theme.DoughCalculator`
follow the template's `<Name>App` and `<Name>Theme` pattern. The code uses the full name; only the
user-visible label is shortened to "Dough Calc".

### Move files without staging
The source folders move with plain filesystem moves, not `git mv`, because git writes are Kenny's.
Git detects the moves as renames when Kenny stages the result.

### Tests
No behaviour changes, so no new tests. The existing unit and UI tests move with the package and must
still pass. The instrumented test that asserts the installed package name now expects
`com.example.doughcalculator`, which pins the new application ID.

## Risks / Trade-offs

- [The old Pizza app stays installed next to Dough Calc, with its own data and home-screen icon.] →
  Uninstall it by hand on each device, once its recipes are no longer needed.
- [Generated code from the old namespace lingers in the build folder and confuses the IDE.] → A
  clean build before `./gradlew check`.
- [A missed reference to the old package compiles in one source set but not another.] → The search
  for `com.example.pizza` and `Pizza` across the repository is a task, `./gradlew check` compiles main
  and unit test sources, and the instrumented tests are compiled and run separately.

## Migration Plan

None: Dough Calc is a new app with empty storage. Rolling back means reinstalling the old Pizza app,
which still has its own data as long as it was not uninstalled.
