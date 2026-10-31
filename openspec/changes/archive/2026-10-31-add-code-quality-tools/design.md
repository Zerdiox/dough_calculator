# Design

## Context

See proposal.md for why. The project has one app module, uses AGP 9 with built-in Kotlin, and the root
build file already applies Spotless and declares the other plugins.

## Goals / Non-Goals

**Goals:**
- Both tools run from one Gradle command each and report on this project's Kotlin.

**Non-Goals:**
- Failing the build on findings, before Kenny has seen them.

## Decisions

**Apply both at the root.** Dependency Analysis needs the Android and Kotlin plugins loaded in the same
or a parent classloader, and the root build file already declares them. Its settings plugin form was not
needed. The plugin also has to be applied to the app module to analyse it.

**`de.aaschmid.cpd` for copy-paste detection.** PMD's own docs cover its command line and not Gradle,
and this is a dedicated Gradle plugin for it. Its last release is from November 2024, so if it
doesn't work with this Gradle, the fallback is a small task that runs PMD's command line.

**CPD reads the Kotlin sources directly.** The plugin finds sources through Gradle's Java plugin, which an
Android module doesn't have, so the task names `app/src` itself. Production code and tests are both
checked, since DRY applies to both.

## Risks / Trade-offs

- [Dependency Analysis may misjudge AGP 9's built-in Kotlin] → Each finding is checked against the code
  before it is reported as real.
- [A token threshold that is too low reports harmless repetition] → Start from the plugin's documented
  default and report what comes up.
- [Dependency Analysis says it is only known to work up to AGP 9.3.1, and the project is on 9.4.1] → Its
  advice is checked, not applied blindly: it wrongly suggests making a library the debug build needs
  release-only.
- [The CPD plugin uses a Gradle API that Gradle 10 removes, and shows a deprecation warning] → Replace it
  with a small task around PMD's command line before moving to Gradle 10.
