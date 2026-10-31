# Tasks

## 1. Duplicates

- [x] 1.1 Replace the two field implementations with one shared field, and verify the existing stepper and Convert tests pass.
- [x] 1.2 Add one calculator setup for on-device tests and use it in all four test classes, and verify they pass.
- [x] 1.3 Add one salt and yeast recipe for unit tests and use it in the three test classes, and verify they pass.

## 2. Dependencies

- [x] 2.1 Remove `core-ktx` and `lifecycle-runtime-ktx`, and verify the debug and release builds succeed. Restore any that is needed.

## 3. Final checks

- [x] 3.1 Run `./gradlew check` and verify it is green.
- [x] 3.2 Run the full on-device suite and verify it is green.
- [x] 3.3 Install the debug build on the emulator, and verify the app opens and its typed fields look the same.
- [x] 3.4 Run `cpdCheck` and `buildHealth`, and verify the fixed findings are gone.

## 4. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
