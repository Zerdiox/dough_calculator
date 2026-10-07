# Apply notes

Decisions taken during implementation that the plan didn't spell out, for Kenny to review.

- **1.4:** No instrumented test depended on the 100% limit (the `120`/`1200` hits are grams), so
  none needed changing. `compileDebugAndroidTestKotlin` stays green.
- **2.2:** "Portions under 5 g" only shows once the flour is valid and every other weight can be
  read. While a field still says "Enter grams", the total isn't known yet, so the portions message
  stays quiet (both buttons are disabled anyway).
- **2.2:** Grams fields take at most six whole digits, the same as the portion weight field.
- **2.3:** Besides the dropdown filter, the Undo plumbing is shared too: `Removal` became generic
  (`Removal<T>`), with a shared `restore` helper and a shared "new ingredient" display name, so the
  calculator's editor and Convert use the same code.
- **3.3:** The ViewModel's own flows can't run on the JVM in these tests (no Main dispatcher), so,
  like the existing ViewModel tests, they check the stored data rather than `recipe` and
  `loadedRecipe`. I wrote these tests and the code in one step, so I didn't see them fail first.
- **3.3:** The draft holder isn't nullable: Convert's draft is reset when the user taps Convert on
  the calculator, not when the screen is composed, so rotating keeps it. If Android kills the app
  while Convert is open, the screen comes back empty, which matches "not remembered after the app
  is closed".
- **4.3:** The Convert icon in the calculator's top bar is Material's `swap_horiz` (two arrows),
  added as `ic_swap_horiz.xml` like the other icons. Its label for screen readers is "Convert
  recipe". It sits first, before Save and Saved recipes.
- **4.3:** The messages for weights ("Enter grams", "Over 200% of the flour", "Portions under 5 g")
  show as a line under the row, aligned right, instead of inside the narrow grams field where they
  would wrap.
- **4.5:** Detekt's default limits (functions per class, return statements, condition size,
  function length) shaped the final code, which differs a little from design.md:
  - Row editing lives in its own class, `ConvertedRows`, next to `ConversionDraft` (the same split
    as the calculator's ingredient editor). The draft has a public constructor, and plain `copy()`
    replaces the trivial setters.
  - The ViewModel has no separate `saveConvertedRecipe`. Its `saveRecipe` takes the same
    `linkCalculator` flag as the repository, and Convert passes `false`.
  - The Convert screen's body is its own composable.
  - No Detekt rule was suppressed or loosened.
- **5 (manual checks):** All passed on the Pixel_9 emulator. Notes:
  - Water 1100 g with Flour 500 g, and the "Use in calculator" unlinking, were checked with
    screenshots. For the unlinking I first saved the calculator as "Friday night", so the "Update"
    option was there before and gone after.
  - Flour typed as 500 g shows as 499 g in the table, from rounding the percentages (see design.md).
  - The emulator restarted once mid-run and lost its data, so "Friday night" was saved again before
    the 5.2 checks.
- **Found during manual checks, not fixed (follow-up candidate):** after tapping a row's remove
  button while its grams field has focus, the keyboard stays up and covers the "Removed … / Undo"
  message. The message only shows once the keyboard is closed, and by then Undo may have timed out.
  The calculator's ingredient editing has the same behaviour, so it isn't new with Convert.
