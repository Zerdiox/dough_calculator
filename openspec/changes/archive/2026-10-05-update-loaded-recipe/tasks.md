# Tasks

## 1. Before starting

- [x] 1.1 Prove that `DoughViewModel` can run in a plain JVM unit test: build it through
  `ViewModelProvider.create` with a `ViewModelStore`, its `DoughRepository` on a DataStore on a
  temporary file (as in `DoughDataStoreTest`), call `updateRecipe`, and read the result back from the
  DataStore with `runBlocking`. Verify the test passes. If it can't run on the JVM, remove it, write
  down why in the completion summary, skip 3.1, and add the cancel and close cases of 3.1 to the
  manual checklist in 5.2.

## 2. Stored link and recipe writes

- [x] 2.1 Add tests. In `DoughDataFormatTest`: data without a loaded recipe doesn't write
  `loadedRecipeId`; the existing stored JSON loads with no loaded recipe. In a new
  `DoughRepositoryTest` on a DataStore on a temporary file: loading a recipe stores its values and
  its id in the same write; saving as new adds the recipe and links it; updating replaces the values
  of the recipe with that id, keeping its id, name and place, and adds no recipe; updating an id that
  no longer exists changes nothing; the loaded recipe is found while it exists and is `null`
  once it is deleted. Run `./gradlew spotlessApply` after creating the file (F10).
  Verify the new tests fail.
- [x] 2.2 Add `loadedRecipeId: String? = null` to the stored data, add the load and update
  functions to the repository, and make saving as new set the link in the same `updateData`. Verify
  the tests from 2.1 and all earlier tests pass, with no format upgrade step added.

## 3. Writes that outlive the screen

- [x] 3.1 Add ViewModel tests on the JVM, with the ViewModel's writes in a test scope (work in
  `viewModelScope` can't run on the JVM without a Main dispatcher, see 1.1): a value changed with
  `updateRecipe` is stored even when the ViewModel is cleared right after; loading a recipe right
  after `updateRecipe` leaves the loaded values and the link stored, not the earlier change. Verify
  the new tests fail.
  Finding the loaded recipe (present while it exists, `null` after it is deleted) is a pure
  function of the stored data and is tested in 2.1 instead.
- [x] 3.2 Add one application-wide `CoroutineScope` to `PizzaApplication`, pass it to the DataStore
  and, through the factory, to the ViewModel, and launch every write in it. Add the loaded recipe as
  a `StateFlow<SavedRecipe?>`, a load function that cancels the pending delayed save before writing,
  and an update function. Verify the tests from 3.1 and all earlier tests pass.

## 4. Choice before saving

- [x] 4.1 Add a Compose UI test for the calculator content in a new androidTest file. With a loaded
  recipe named "Friday night", the save button shows *Update "Friday night"* and *Save as new…*;
  Update calls the update callback once and shows "Updated "Friday night""; *Save as new…* opens the
  name dialog, and saving there calls the save callback with the name; Cancel and Back call neither.
  With no loaded recipe, the save button opens the name dialog directly. Update the calculator call
  in `ResetMessageTest` for the new parameters. Run `./gradlew spotlessApply` after creating the file
  (F10). Verify it fails with `connectedDebugAndroidTest` on the `Pixel_9` emulator.
- [x] 4.2 Add the choice dialog to the calculator content, shown from new parameters for the loaded
  recipe's name and an update callback, cancelled by Back and taps outside. Verify the test from 4.1
  passes on `Pixel_9`.
- [x] 4.3 Wire `PizzaApp`: "Edit in calculator" loads the recipe through the ViewModel, and the
  calculator gets the loaded recipe's name and the update function. Verify with
  `./gradlew assembleDebug` and the manual checks in 5.2.

## 5. Build and manual check

- [x] 5.1 Run `./gradlew check` and verify it is green.
- [x] 5.2 Install the app over the existing install on the `Pixel_9` emulator, with at least two
  saved recipes stored by the previous build, and check:
  - [x] The saved recipes are all still there, and saving opens the name dialog directly
  - [x] Edit "Friday night", change water, tap save: the choice shows *Update "Friday night"*; tap
    it: "Updated "Friday night"" appears, and the list shows "Friday night" with the new water in
    the same place, with no new recipe
  - [x] `adb shell run-as com.example.pizza cat files/datastore/dough.json` shows `loadedRecipeId`
    with the id of "Friday night"
  - [x] Force-stop the app and open it again, tap save: *Update "Friday night"* is offered
  - [x] Cancel, Back and a tap outside the choice dialog each close it and save nothing
  - [x] Choose *Save as new…* with the name "Wetter Friday": it is added, "Friday night" is
    unchanged, and the next save offers *Update "Wetter Friday"*
  - [x] Delete "Wetter Friday", tap save: the name dialog opens directly
  - [x] Change a value and leave with Back straight away
    (`adb shell input swipe …` followed at once by `adb shell input keyevent BACK`), open the app
    again: the new value is still there. Turn the emulator's animations off first; otherwise the
    exit animation outlasts the delay and even the previous build keeps the value

## 6. Follow-up harvest

- [x] 6.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
