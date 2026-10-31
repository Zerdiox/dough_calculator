# Tasks

## 1. Dependency Analysis

- [x] 1.1 Add the plugin to the version catalog and the root build file, and verify `./gradlew buildHealth` runs.
- [x] 1.2 Check each finding against the code and list it for Kenny.

## 2. Copy-paste detection

- [x] 2.1 Add the CPD plugin, set to Kotlin and report-only, and verify `./gradlew cpdCheck` reports on `app/src`.
- [x] 2.2 Check each finding against the code and list it for Kenny.

## 3. Dev build

- [x] 3.0 Give debug builds their own application ID and name, and verify one installs next to the real app on a phone, leaving the real app untouched.

## 4. Final checks

- [x] 3.1 Run `./gradlew check` and verify it is green. The CPD plugin adds itself to `check`, where it reports without being able to fail the build; `buildHealth` stays separate.
- [x] 3.2 Run the full on-device suite and verify it is green.

## 5. Follow-up harvest

- [x] 4.1 List each out-of-scope issue discovered during implementation as a follow-up candidate in the completion summary; record them (see openspec/backlog/followup/README.md) on Kenny's go.
