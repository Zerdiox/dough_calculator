# Tasks

## 1. Recipe model and weights

- [x] 1.1 Write failing tests for count scaling: 2 at 4 portions gives 0.5, 1, 1.5, 3 at 1, 2, 3, 6 portions; never below 0.5; 0 stays 0; returns to 2 at 4 portions
- [x] 1.2 Add the optional count to the ingredient and the current-amount calculation, and verify the 1.1 tests pass
- [x] 1.3 Write failing tests for weights: 1000 g, 60% water, Eggs 2 at 50 g gives flour 562.5 g and adds up to 1000 g; no grams each gives flour 625 g; too-heavy counts give 0 g flour
- [x] 1.4 Update the weights calculation and verify the 1.3 tests pass
- [x] 1.5 Write a storage test that data without a count loads as percentage ingredients with unchanged weights, and that a count round-trips, and verify it passes

## 2. Weights table

- [x] 2.1 Write failing tests for table rows: "× 1.5" with "75 g", "× 2" with no weight, count at 0 hidden, no percentage on count rows
- [x] 2.2 Update the rows and the table, including the "Counted ingredients use up the whole dough weight" message, and verify the 2.1 tests pass

## 3. Calculator

- [x] 3.1 Write failing tests for the count rules: typing 1.5 and "1,5" accepted; 1.3, "abc" and 101 refused with "Enter 0 to 100, in halves"; steps of 0.5 stopping at 0 and 100
- [x] 3.2 Add the count rules and verify the 3.1 tests pass
- [x] 3.3 Write failing editor tests: choosing a kind, restoring on switching back in one edit, invalid grams each blocking Done, Undo restoring count and grams each
- [x] 3.4 Update the ingredient editor and verify the 3.3 tests pass
- [x] 3.5 Add a failing UI test: the count control follows portions, and + sets the amount for the current portions
- [x] 3.6 Show the count control and the edit row's kind choice and "Grams each" field, and verify the 3.5 test passes

## 4. Convert

- [x] 4.1 Write failing conversion tests for the spec scenarios: Eggs 2 at 50 g gives "4 × 225 g"; no grams each gives "4 × 200 g"; changing portions keeps 2; 1.3 shows the message and blocks both buttons
- [x] 4.2 Share the grams parser, update the conversion rules and verify the 4.1 tests pass
- [x] 4.3 Add a failing UI test for choosing "Count" on a row, keeping the typed text and showing "Grams each"
- [x] 4.4 Update the Convert rows, and verify the 4.3 test passes

## 5. Saved ingredients

- [x] 5.1 Write failing storage tests: old name-only data loads as percentage ingredients; a saved count round-trips; ticking adds kind and grams each; an already saved ingredient isn't changed; Undo restores kind and grams each
- [x] 5.2 Add the saved ingredient type, update the repository and view model, and verify the 5.1 tests pass
- [x] 5.3 Write failing tests: picking a saved count sets kind and grams each in the editor; on Convert it does the same and keeps the typed amount; a saved percentage ingredient sets "%" or "Grams"
- [x] 5.4 Apply the saved setup on picking, and verify the 5.3 tests pass
- [x] 5.5 Add a failing UI test for "Count · 50 g each" on the saved ingredients screen, show it, and verify the test passes

## 6. Docs and verification

- [x] 6.1 Describe count ingredients and their saved setup in README.md's "What it does" and in the recipe formula, and verify the text matches the specs
- [x] 6.2 Manual check in the app, on the emulator:
  - [x] Tick Eggs when saving, then pick Eggs in a new recipe and on Convert; both are set up as a count with the same grams each
  - [x] Existing saved ingredients still appear and add as percentages
  - [x] Add Eggs as a count at 2 for 4 portions; 3 portions shows 1.5, and back to 4 shows 2
  - [x] With 50 g each the table shows the weight and the total stays at the set dough weight
  - [x] Clearing "Grams each" leaves flour as if Eggs weren't there
  - [x] Convert a recipe with eggs, save it, and check its full recipe and a restart
  - [x] Existing saved recipes show the same weights as before
  - [x] Rows stay readable on the Convert and edit screens
- [x] 6.3 Run `./gradlew check` and verify it is green

## 7. Follow-up harvest

- [x] 7.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
