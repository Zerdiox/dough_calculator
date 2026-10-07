# Apply notes

Decisions taken during implementation that the plan didn't spell out, for Kenny to review.

- **2.2:** The "Enter the flour weight to see the recipe" card lives next to the weights table in
  `DoughResultCard.kt` (as `FlourNeededCard`), not in `ConvertRecipeScreen.kt`. Adding it there
  went over Detekt's limit of 11 functions per file. No Detekt rule was suppressed or loosened.
- **3 (manual checks):** All passed on the Pixel_9 emulator. Running the instrumented tests
  uninstalled the app first, so the emulator's earlier saved recipes and ingredients are gone.
- **Follow-up harvest:** nothing new. The keyboard covering "Removed … / Undo" is still the only
  candidate, already listed under Out of Scope in proposal.md.
