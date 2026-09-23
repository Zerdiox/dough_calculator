# Dough Calculator

An Android app that works out how much flour, water, salt, yeast and oil to
weigh out for a batch of pizza dough.

Recipes are stored as baker's percentages, where every ingredient is expressed
relative to the flour weight and flour itself is always 100%. That makes a
recipe independent of batch size: you set how many pizzas you want and how
heavy each dough ball should be, and the app solves for the flour weight that
produces that total, then scales the rest.

```
flour = total dough / (1 + (hydration + salt + yeast + oil) / 100)
```

## What it does

- **Calculator** with steppers and sliders for pizza count, ball weight
  (100 to 500 g), hydration (50 to 90%), salt (0 to 5%), yeast (0 to 3%) and
  oil (0 to 6%). Ingredient weights update as you drag.
- **Saved recipes**, each under a name you choose.
- **Full recipe screen** for while you're actually making the dough. The
  display stays on, and you can change the batch size there without editing
  the saved recipe.

The current recipe and the saved ones are written to `dough.json` via DataStore,
so they survive a restart. The app asks for no permissions and has no account;
it does inherit Android's Auto Backup, so `dough.json` rides along to Google
Drive with the rest of the device backup.

## Building

Needs the Android SDK. The Gradle daemon is pinned to Java 25, which Gradle
downloads on the first run if you do not have it. Everything goes through the
wrapper:

```
./gradlew :app:assembleDebug
```

`minSdk` is 36, so the app installs on Android 16 and newer.

Release builds are signed only if a `keystore.properties` exists in the project
root, holding `storeFile`, `storePassword`, `keyAlias` and `keyPassword`. That
file and the keystore it points at are kept out of git, so a fresh clone builds
debug but produces an unsigned release.

## Stack

Kotlin and Jetpack Compose with Material 3, `ViewModel` for state, DataStore
plus kotlinx.serialization for storage, and Navigation 3 for the three screens.
Detekt and Android Lint run as part of `check`, with lint warnings treated as
errors.

Personal project, built for one kitchen.
