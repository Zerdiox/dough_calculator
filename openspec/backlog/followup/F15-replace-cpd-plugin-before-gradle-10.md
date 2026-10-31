---
id: F15
title: Replace the CPD Gradle plugin before moving to Gradle 10
found: 2026-10-31
source: add-code-quality-tools
capability:
location: build.gradle.kts (cpd plugin)
type: tech-debt
size: S
---

## What
The `de.aaschmid.cpd` plugin calls `ReportingExtension.file(String)`, which Gradle 10 removes, so
`./gradlew check` prints a deprecation warning. Its last release is from November 2024.

## Why it matters
The build will likely break on the move to Gradle 10, since the plugin adds itself to `check`.

## Notes
The fallback is a small task around PMD's own command line, which supports Kotlin. It should keep
reporting without failing the build, and read `app/src` directly, since an Android module has no Java
plugin sources.
