# Proposal

## Why

The copy-paste and dependency reports found repeated code and two libraries nothing uses directly.
Repetition means one look or one test setup has to be changed in several places.

## What Changes

- One shared compact field for typed values with a unit, used by the steppers and the grams fields.
- One shared calculator setup for on-device tests.
- One shared salt and yeast recipe for unit tests.
- Remove `core-ktx` and `lifecycle-runtime-ktx`, if the app builds and passes its tests without them.

## Capabilities

### New Capabilities

None: the app's behaviour doesn't change.

### Modified Capabilities

None.

## Out of Scope / Deferred

- The portions cards and the scrolling columns: similar layouts with different contents, where sharing
  would add more than it removes.
- The other dependency advice: `reorderable` as release-only is wrong, and declaring indirect libraries
  adds upkeep.

## Impact

- **Code:** `ValueStepper.kt`, `CountFields.kt`, and several test files.
- **Build:** two fewer direct dependencies.
