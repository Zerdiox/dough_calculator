# Proposal

## Why

When the app can't read its stored data, it starts over with the default values and immediately
overwrites the file on disk. Every saved recipe is gone, and nothing tells the user. Today that only
happens if the file is damaged. The coming per-recipe ingredients change will change what a saved
recipe contains, and a format change that older data can't be read into would wipe every saved
recipe the same way. The app needs a safe way to change its format, and a safety net for when that
goes wrong, before the ingredient change starts.

## What Changes

- Stored data records which format version it was written in. Data saved before this change has no
  version and counts as the first version, so it loads exactly as it does today.
- When the app reads data from an older version, it upgrades it step by step to the current version
  before loading it. This change adds the upgrade mechanism but no upgrade steps; the ingredient
  change adds the first one.
- When stored data can't be read (it's damaged, can't be upgraded, or comes from a newer version of
  the app), the app first keeps a copy of it on the phone, then starts with the default values.
  Copies are never deleted automatically, so the recipes can be recovered by hand.
- After such a reset, the calculator shows a message saying the saved recipes couldn't be read, the
  app started fresh, and a copy of the old data was kept. The message stays until the user
  dismisses it with OK, and shows again on the next launch if the app closed before it was seen.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `saved-recipes`: adds a requirement that the user is told when the saved recipes couldn't be read
  and the app had to start fresh.

## Impact

- Reading and writing the stored data: a format version, an upgrade step before decoding, and a
  copy of the file when reading fails.
- The calculator screen: a new dialog, and the state behind it.
- Stored data gains a version and a flag for the unseen reset message. Existing data loads with the
  same values and is not rewritten until the next save.
- No new dependencies and no new permissions.
- Builds on `rename-to-portions`, which adds the stored-format test. That change lands first.

## Out of Scope / Deferred

- Reporting a failed read to the developer. The app has no internet access and no account, and
  that stays so; the message and the kept copy are enough.
- Migrating saved recipes to per-recipe ingredients. The ingredient change adds that as the first
  upgrade step.
- Recovering a kept copy from inside the app. Recovery stays a manual step with Android Studio.
- Cleaning up old copies. Failures should be rare, and each copy is small.

## Resolves

- **F5**: unreadable stored data silently resets all recipes.
