# Tasks

## 1. Before starting

- [x] 1.1 Check that `rename-to-portions` is finished: all of its tasks are checked, and
  `DoughDataFormatTest` exists and passes. Verify with `./gradlew testDebugUnitTest`.
- [x] 1.2 Prove that a DataStore built with `DataStoreFactory.create` on a temporary file can write
  and read data in a plain JVM unit test (`TemporaryFolder`, `runBlocking`). Verify the test
  passes. If it can't run on the JVM, remove it, write down why in the completion summary, and in
  3.1 test the handler without DataStore.

## 2. Versioned format

- [x] 2.1 Extend `DoughDataFormatTest`: a newly written file contains `"version":1`; the existing
  unversioned JSON still loads with the same values; a file with a version newer than the current
  one, malformed JSON, a missing required field and a value of the wrong type each fail with
  `CorruptionException`; with a fake list of one upgrade step, a version 1 file is upgraded and
  then decoded, and a step that throws `IllegalArgumentException` fails with `CorruptionException`.
  Verify the new tests fail on the current code.
- [x] 2.2 In the serializer, add the version when writing, and when reading, read it (missing
  means 1), run the upgrade steps (an empty list for now, passed as a parameter with that list as
  the default) and then decode. Turn `SerializationException`, `IllegalArgumentException` and a
  newer version into `CorruptionException`, and nothing broader. Verify every test in
  `DoughDataFormatTest` passes, including the existing ones with their JSON unchanged.

## 3. Keep a copy of unreadable data

- [x] 3.1 Add tests: the corruption handler copies the data file to `dough-unreadable-<millis>.json`
  in the same folder with identical bytes, and returns the defaults with `resetMessagePending`
  set; a second failure adds a second copy and leaves the first alone; when the copy can't be made
  (delete the data file before calling the handler, which fails the same way on every OS), the
  handler throws. If 1.2 succeeded, also test the whole path: a DataStore on a file holding broken
  JSON reads the defaults with the flag set, a copy with the broken bytes exists, and the main file
  is readable again afterwards; and when two collectors read the broken file at the same time,
  exactly one copy is made. Add to `DoughDataFormatTest` that data without the flag
  set doesn't write `resetMessagePending`. Verify the new tests fail.
- [x] 3.2 Add `resetMessagePending` (default `false`) to the stored data, write the copying
  handler, and build the DataStore with `DataStoreFactory.create` and
  `context.dataStoreFile("dough.json")` so the file name and location don't change. Keep the single
  instance in a new `PizzaApplication`, register it in the manifest, have the ViewModel factory
  read it from there, and remove the `by dataStore` delegate. Verify the tests from 3.1 and all
  earlier tests pass.

## 4. Reset message on the calculator

- [x] 4.1 Add a Compose UI test for the calculator content: with the reset message on, it shows
  "Saved recipes couldn't be read" and the body saying the app started fresh and a copy was kept;
  tapping OK calls the dismiss callback once; pressing back doesn't call it. With the message off,
  there's no dialog. Verify it fails with `connectedDebugAndroidTest` on an emulator or device.
- [x] 4.2 Add the dialog to the calculator content: an `AlertDialog` with one OK button and an
  `onDismissRequest` that does nothing, shown from a new parameter with a dismiss callback. Verify
  the test from 4.1 passes.
- [x] 4.3 Expose the flag from the ViewModel as a `StateFlow<Boolean>`, add a dismiss function that
  clears it with `updateData`, and wire both through `PizzaApp` into the calculator. No unit test
  for this step, since the build has no coroutine test library and the wiring is thin. Verify it by
  building (`./gradlew assembleDebug`) and with the manual checks in 5.2.

## 5. Build and manual check

- [x] 5.1 Run `./gradlew check` and verify it is green.
- [x] 5.2 Install the app over the existing install (don't uninstall) and check:
  - [x] Recipes saved before the update are all still there with the same values, and no message
    appears
  - [x] Save a recipe, then confirm the stored file now starts with the version:
    `adb shell run-as com.example.pizza cat files/datastore/dough.json`
  - [x] Force-stop the app, break the file with
    `adb shell run-as com.example.pizza sh -c "echo broken > files/datastore/dough.json"`, then
    open the app: the calculator shows the defaults and the "Saved recipes couldn't be read"
    message
  - [x] `adb shell run-as com.example.pizza ls files/datastore` lists a
    `dough-unreadable-<number>.json`, and `cat` on it shows `broken`
  - [x] Back and a tap outside the dialog leave the message open
  - [x] Force-stop the app before tapping OK and open it again: the message shows again
  - [x] Tap OK, force-stop, open again: no message; the saved recipes list is empty
  - [x] Save a new recipe: it appears in the list, and the unreadable copy is still in
    `files/datastore`
  - [x] Break the file a second time and open the app: a second copy appears next to the first

## 6. Follow-up harvest

- [x] 6.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in
  the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
