# Tasks

## 1. Putting a recipe back

- [x] 1.1 Add tests to `DoughRepositoryTest`: restoring a deleted recipe puts it back at its old
  position with the same id, name and values; a position past the end puts it last; restoring a
  recipe whose id is still in the list changes nothing; restoring the recipe the calculator was
  loaded from makes it the loaded recipe again. Verify the new tests fail.
- [x] 1.2 Add the restore function to the repository and a ViewModel function that launches it in
  the write scope. Verify the tests from 1.1 and all earlier unit tests pass.

## 2. Message with Undo

- [x] 2.1 Add a Compose UI test for the saved recipes screen in a new androidTest file: confirming a
  delete shows "Deleted "Friday night"" with Undo; Undo calls the restore callback once with the
  recipe and its position; Cancel in the dialog deletes nothing and shows no message; deleting a
  second recipe while the first message shows replaces it with a message naming the second. Run
  `./gradlew spotlessApply` after creating the file (F10). Verify it fails with
  `connectedDebugAndroidTest` on the `Pixel_9` emulator.
- [x] 2.2 Add the message area, the dismiss-then-show message with `SnackbarDuration.Long`, and
  the restore callback to the saved recipes screen. Verify the test from 2.1 passes on `Pixel_9`.
- [x] 2.3 Wire the restore callback in `PizzaApp` to the ViewModel. Verify with
  `./gradlew assembleDebug` and the manual checks in 3.2.

## 3. Build and manual check

- [x] 3.1 Run `./gradlew check` and verify it is green.
- [x] 3.2 Install the app over the existing install on the `Pixel_9` emulator, with at least three
  saved recipes, and check:
  - [x] Delete the middle recipe and confirm: "Deleted "<name>"" with Undo appears; tap Undo: it is
    back in the middle with the same summary, and
    `adb shell run-as com.example.pizza cat files/datastore/dough.json` shows it with its old id
  - [x] Delete a recipe and wait: the message goes away after about ten seconds and the recipe
    stays deleted
  - [x] Delete a recipe, then a second one while the message shows: the message names the second,
    Undo brings back only the second
  - [x] Delete a recipe and go back to the calculator before tapping Undo: back in the list, the
    recipe stays deleted
  - [x] Load a recipe into the calculator, delete it in the list, tap Undo, go back and save:
    updating it is offered
  - [x] Cancel in the confirmation dialog: nothing is deleted and no message shows

## 4. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
