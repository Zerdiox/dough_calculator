# Design

## Context

See proposal.md for why. The app stores one JSON file through DataStore: the calculator's recipe
and the saved recipes. The serializer ignores unknown keys and turns any decoding failure into a
`CorruptionException`. The corruption handler then returns the defaults, and DataStore writes them
over the file straight away, before the user has done anything.

DataStore calls the corruption handler before it writes the replacement, so the unreadable file is
still intact while the handler runs. The handler is a plain lambda that runs on DataStore's IO
scope. `DataStoreFactory.create` accepts only a `ReplaceFileCorruptionHandler`, and it computes the
file path lazily, on the first read.

The stored data is written with kotlinx.serialization's default settings, so a property that holds
its default value is not written to the file.

## Goals / Non-Goals

**Goals:**
- A way to change the stored format that the next change can use by adding one upgrade step.
- No read failure destroys data that hasn't been copied first.
- Files written before this change load unchanged.

**Non-Goals:**
- Any real upgrade step. The first one comes with the ingredient change.
- Multi-process access. The app has one process.

## Decisions

### The version is added by the serializer, not the data model
When writing, the serializer encodes the data to a JSON object and adds `"version": <current>`.
When reading, it parses the file to a JSON object, reads `version` (a missing version means 1),
runs the upgrade steps from that version to the current one, and only then decodes the data.

The version describes the file, not the recipes, so it stays out of the model the rest of the app
uses.

- *Alternative: a `version` property on the data model with `@EncodeDefault`.* That works too, but
  the upgrade steps have to run before decoding anyway. Reading the version in the same place keeps
  the whole format concern in one spot, and the model can't be built with a wrong version.
- *Alternative: a plain property whose default is the current version.* This is wrong: default
  values aren't written, so every file would look like version 1 forever.

### Upgrade steps are an ordered list; the current version follows from it
Each step takes the JSON object of one version and returns the next version's JSON object. The
current version is 1 plus the number of steps, so adding a step is the whole job of a future format
change. The list is empty in this change. The decoding function takes the list as a parameter that
defaults to the real one, so tests can check the mechanism with fake steps.

- *Alternative: DataStore's `DataMigration`.* It runs on data that has already been decoded, so it
  can't help once the old shape no longer decodes, and that's exactly the case that needs help.

### What counts as unreadable
The serializer turns each of these into a `CorruptionException`:
- JSON that doesn't parse, or data that doesn't decode (`SerializationException`)
- a value of the wrong type, including one met inside an upgrade step (`IllegalArgumentException`)
- a version newer than the current one, for example after installing an older build

It catches nothing broader. An `Error` such as running out of memory still crashes.

### The handler copies the file, then returns defaults with a flag
The DataStore is built with `DataStoreFactory.create`, so the handler can close over the data
file. The handler copies the file to `dough-unreadable-<epoch millis>.json` in the same folder,
with `copyTo(overwrite = false)`, then returns the defaults with `resetMessagePending = true`.
Milliseconds keep two failures from colliding, and never overwriting keeps every copy.

- *Alternative: keep the `by dataStore` delegate.* Its corruption handler is created without a
  `Context`, so it can't find the file to copy.
- *Alternative: the serializer carries the raw bytes in the exception.* That works without the
  file, but it moves file handling into the serializer and depends on an exception's cause for
  data.

### The single DataStore lives in an Application class
The `by dataStore` delegate also guaranteed a single DataStore per file, and DataStore fails when
two instances open the same file. A `PizzaApplication` holds it in a `lazy` property and is
registered in the manifest. The ViewModel factory already has the application, so it reads the
DataStore from there.

- *Alternative: a hand-written process-wide singleton behind the existing `Context` extension.*
  It would keep call sites as they are, but it rebuilds the delegate's locking by hand. The
  Application class is the standard Android place for app-wide objects and needs no locking of our
  own.

### The reset message is a stored flag
`resetMessagePending` lives in the stored data with a default of `false`, so it isn't written
until a reset sets it. Because it's stored, it survives the app closing before the user sees the
message. The ViewModel exposes it as a `StateFlow<Boolean>` and clears it with an `updateData`
when the user taps OK.

- *Alternative: keep it only in memory.* It would be lost if the app closed before the user saw
  it, which the spec rules out.

### The dialog closes only on OK
The calculator shows a Material 3 `AlertDialog` with one OK button, and its `onDismissRequest`
does nothing. Back and taps outside the dialog would otherwise close it without clearing the
flag, and it would come back on the next launch. It uses the same `AlertDialog` as saving a
recipe.

### If the copy fails, nothing is overwritten
If copying throws, the handler lets the exception through and DataStore doesn't write the
defaults. The data is only replaced once a copy exists. Reading then falls back to the
repository's existing `IOException` handling (defaults on screen, nothing written). Saves will keep
failing until the problem is gone.

- *Alternative: reset anyway.* The message would then claim a copy that doesn't exist, and the
  data would be lost, which is the very thing this change prevents.

## Risks / Trade-offs

- [The copy fails (for example, the disk is full) and later saves throw inside the ViewModel] →
  Accepted. Copying a small file fails only when writing would fail too. Losing the data would be
  worse than a failed save.
- [Copies build up] → Each one is a few kilobytes and needs a failure to create. Clean-up is out of
  scope.
- [Copies go into Android's auto backup, since `allowBackup` is on with the default rules] →
  Harmless. It's the same data as the main file.
- [A future upgrade step has a bug] → It surfaces as unreadable data: the file is copied and the
  message shows, instead of the recipes being lost silently. The version test and step tests
  guard against this.
- [The Android DataStore artifact may not run in a plain JVM unit test] → The first task proves an
  end-to-end round trip in a unit test. If it can't run, the decoding function and the handler are
  tested separately, and the manual checklist covers the whole flow.

## Migration Plan

Nothing to migrate. Files without a version load as version 1 and are rewritten with
`"version": 1` on the next save. Rolling back to the previous build still works, because it
ignores the unknown `version` key.
