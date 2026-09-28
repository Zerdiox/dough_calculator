# Tasks

## 1. Check the specs

- [x] 1.1 Run `openspec validate add-baseline-specs --strict` and verify it reports the change as
  valid
- [x] 1.2 Read each of the four specs against the app's code and verify every requirement either
  matches today's behaviour or is covered by one of F1–F5; list any other difference as a follow-up
  candidate

## 2. Manual check in the app

- [x] 2.1 Install a debug build, then walk through this checklist and verify each item behaves as
  the specs say, apart from F1–F5:
  - [x] First launch shows 4 × 250 g, water 62%, salt 3%, yeast 0.2%, olive oil 0%
  - [x] The weights table shows "TOTAL DOUGH", "4 × 250 g" and "1000 g", with no Olive oil row
  - [x] Yeast shows one decimal (e.g. "1.2 g"); flour shows whole grams
  - [x] Each slider snaps to its step; − and + move one step and disable at the range ends
  - [x] Close and reopen the app: the calculator keeps its values
  - [x] Save "Friday night": Save is disabled while the name is blank, a "Saved" message appears,
    and saving a second "Friday night" is allowed
  - [x] Saved recipes list shows oldest first, with "4 × 250 g · 62% water"; with no recipes it
    shows the empty message
  - [x] Tapping a recipe opens its full recipe titled with its name; changing portions there does
    not change the saved recipe or the calculator
  - [x] "Start kneading" opens "Full recipe" with the calculator's values; the screen stays on and
    says so
  - [x] Deleting asks for confirmation; Cancel keeps the recipe

## 3. Build

- [x] 3.1 Run `./gradlew check` and verify it is green (this change touches no code)

## 4. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
