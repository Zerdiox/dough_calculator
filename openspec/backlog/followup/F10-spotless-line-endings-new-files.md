---
id: F10
title: Spotless flags line endings on new files on Windows
found: 2026-09-28
source: rename-to-portions
capability:
location: build.gradle.kts:22
type: tech-debt
size: S
---

## What
`./gradlew check` fails in `spotlessKotlinCheck` on a newly created `.kt` file whose only difference
is line endings. With `core.autocrlf=true`, Spotless expects CRLF in the working copy, but files
written by tools (and Claude) are LF. `./gradlew spotlessApply` fixes it by converting the file to
CRLF.

## Why it matters
Every new Kotlin file can fail the check gate for a reason that has nothing to do with the code,
which costs a confusing extra build each time.

## Notes
Seen on two new test files in rename-to-portions. Git normalizes to LF on commit (`* text=auto`), so
nothing wrong reaches the repo. Possible fixes: set Spotless `lineEndings` to a fixed value
(e.g. `UNIX`) together with `eol=lf` for `*.kt` in .gitattributes, or document running
`spotlessApply` after creating files.
