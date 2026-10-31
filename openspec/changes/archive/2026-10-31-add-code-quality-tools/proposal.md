# Proposal

## Why

The existing checks don't look for copy-pasted code or for unused and wrongly declared dependencies,
so a DRY problem or a leftover library can go unnoticed.

## What Changes

- Add the Dependency Analysis Gradle plugin, run with `buildHealth`.
- Add PMD's copy-paste detector for Kotlin, run with `cpdCheck`.
- Both only report for now and can't fail the build. The CPD plugin adds itself to `./gradlew check`;
  `buildHealth` stays separate.

- Debug builds install next to the real app as "Dough Calc Dev", so trying a build can't touch its
  saved recipes.

## Capabilities

### New Capabilities

None: this is build tooling, and the app's behaviour doesn't change.

### Modified Capabilities

None.

## Out of Scope / Deferred

- Kover: it can't measure on-device tests, so the screens would look untested.
- Konsist and Compose Stability Analyzer: little to gain in a one-module app with no performance
  problem.
- Compose screenshot tests: the replacement for the unmaintained plugin needs AGP 9.5.
- Acting on the findings, which Kenny decides after seeing them.

## Impact

- **Build:** two new plugins in the version catalog and the root build file.
- **App:** no change to the app or what it ships with.
