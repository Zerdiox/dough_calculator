# Tasks

## 1. Package, namespace and application ID

- [x] 1.1 Change `namespace` in the app's build file to `com.example.doughcalculator`. Move the
  `com/example/pizza` source folders to `com/example/doughcalculator` in `main`, `test` and
  `androidTest` with plain moves (no `git mv`), and update every `package` and `import` line. Verify
  the main and unit test sources compile.
- [x] 1.2 Change `applicationId` to `com.example.doughcalculator`, and make `ExampleInstrumentedTest`
  expect that package name. Verify that searching the repository (outside `openspec/`) for
  `com.example.pizza` finds nothing.

## 2. App, class and theme names

- [x] 2.1 Rename `PizzaApplication` to `DoughCalculatorApplication`, `PizzaApp` to
  `DoughCalculatorApp`, `PizzaTheme` to `DoughCalculatorTheme`, `PizzaColorScheme` to
  `DoughCalculatorColorScheme` and `PizzaShapes` to `DoughCalculatorShapes`, renaming the files that
  carry those names, and update the manifest's application class. Verify the main and unit test
  sources compile.
- [x] 2.2 Rename the XML style `Theme.Pizza` to `Theme.DoughCalculator` and update both uses in the
  manifest. Verify the app's resources build.
- [x] 2.3 Set `app_name` to "Dough Calc" and `rootProject.name` to "Dough Calculator". Verify Gradle
  syncs.
- [x] 2.4 Search the repository (outside `openspec/`) case-insensitively for `pizza`. Verify the only
  hits are the stored-data key `pizzaCount` and the test data using it, the "Friday pizza night"
  sample names, the two colour comments that call the palette a pizza palette, and the README (F12).

## 3. Build and manual check

- [x] 3.1 Run a clean build and `./gradlew check`, and verify it is green.
- [x] 3.2 Run `connectedDebugAndroidTest` on the emulator and verify every instrumented test passes,
  including the package name check.
- [x] 3.3 Install the new build on the emulator and check:
  - [x] It installs as a new app named "Dough Calc", next to the old Pizza app
  - [x] "Dough Calc" fits under the icon in the app drawer without being cut off
  - [x] Settings > Apps lists it as "Dough Calc"
  - [x] It opens on the calculator with the default recipe and no saved recipes
  - [ ] The colours and splash screen look the same as before
  - [x] A recipe saved in Dough Calc is still there after closing and reopening the app
- [x] 3.4 Uninstall the old Pizza app from the emulator and verify Dough Calc still opens.

## 4. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
