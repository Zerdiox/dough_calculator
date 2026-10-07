# Dough Calculator

[![Release](https://github.com/Zerdiox/dough_calculator/actions/workflows/release.yml/badge.svg)](https://github.com/Zerdiox/dough_calculator/actions/workflows/release.yml)
![Android 16 and newer](https://img.shields.io/badge/Android-16%2B-3DDC84?logo=android&logoColor=white)
![Kotlin 2.4.20](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white)
![Compose BOM 2026.09.00](https://img.shields.io/badge/Compose%20BOM-2026.09.00-4285F4?logo=jetpackcompose&logoColor=white)
![Android Gradle plugin 9.4.1](https://img.shields.io/badge/AGP-9.4.1-3DDC84?logo=androidstudio&logoColor=white)
![Gradle 9.6.0](https://img.shields.io/badge/Gradle-9.6.0-02303A?logo=gradle&logoColor=white)

An Android app that works out how much of each ingredient to weigh out for a
batch of dough: flour, water, and whatever else the recipe calls for.

Recipes are stored as baker's percentages, where every ingredient is expressed
relative to the flour weight and flour itself is always 100%. That makes a
recipe independent of batch size: you set how many portions you want and how
heavy each portion should be, and the app solves for the flour weight that
produces that total, then scales the rest.

```
flour = total dough / (1 + (water + every other ingredient) / 100)
```

## What it does

- **Calculator** for how many portions to make, how heavy each one is, and the
  recipe's percentages. Water is always there; every other ingredient is one
  you add, name and order yourself. Percentages are typed in, as precisely as
  the recipe needs, and the weights update as you type.
- **Convert** for recipes written in grams, as books and websites give them.
  Type in the weights and the portions, and the app works out the baker's
  percentages, ready to use in the calculator or keep as a saved recipe.
- **Saved recipes**, each under a name you choose, to follow, load back into
  the calculator to change, or delete.
- **Saved ingredients**, a short list of names to pick from when adding an
  ingredient, so the usual ones don't need typing. A name joins the list when
  you tick it while saving a recipe.
- **Full recipe screen** for while you're actually making the dough. The
  display stays on, and you can change the batch size there without editing
  the saved recipe.

The current recipe, the saved recipes and the saved ingredients are written to
`dough.json` via DataStore, so they survive a restart. The app asks for no
permissions and has no account; it does inherit Android's Auto Backup, so
`dough.json` rides along to Google Drive with the rest of the device backup.

## Building

The least fiddly route is Android Studio: clone the repo, `File > Open`, and it
writes the SDK path for you and offers to download anything missing. Then Run.

From the command line you have to point the build at your SDK first, because
`local.properties` is machine-specific and deliberately not in git:

```
git clone https://github.com/Zerdiox/dough_calculator.git
cd dough_calculator
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
./gradlew :app:assembleDebug
```

Skip that third line and the build stops with `SDK location not found`. On
Windows, write the path with forward slashes: a `.properties` file reads a
backslash as an escape character, so `C:\Users\...` fails less helpfully, with
`java.io.IOException: Invalid file path`.

Nothing else needs installing. The wrapper fetches Gradle, the daemon's Java 25
comes down on the first run, and debug builds need no signing key. The APK ends
up in `app/build/outputs/apk/debug/`.

`minSdk` is 36, so the app only installs on Android 16 and newer, which is a
narrow slice of phones today.

Release builds are signed only if a `keystore.properties` exists in the project
root, holding `storeFile`, `storePassword`, `keyAlias` and `keyPassword`. That
file and the keystore it points at are kept out of git, so a fresh clone builds
debug but produces an unsigned release.

Pushing a `v*` tag runs `.github/workflows/release.yml`, which rebuilds the
signed APK and attaches it to a GitHub release. It reads the same four values
from repository secrets instead: `KEYSTORE_BASE64` (the keystore itself,
base64-encoded), `KEYSTORE_PASSWORD`, `KEY_ALIAS` and `KEY_PASSWORD`.

## Stack

Kotlin and Jetpack Compose with Material 3, `ViewModel` for state, DataStore
plus kotlinx.serialization for storage, and Navigation 3 for the five screens.
Detekt and Android Lint run as part of `check`, with lint warnings treated as
errors.

Personal project, built for one kitchen.
