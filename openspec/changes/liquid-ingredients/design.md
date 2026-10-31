# Design

## Context

See proposal.md for motivation and the specs for behaviour. Today a recipe stores water's percentage
in a field called `hydration`, separate from `ingredients`, and `DoughAmounts`, the weights table, the
name rules, the Convert draft and the ingredients card each handle it separately.

Stored data is JSON with a format version and a list of upgrades, each writing its version's shape
directly. Values equal to their default aren't stored, so a calculator never changed has no
`currentRecipe`, and a saved-ingredient list never changed has no `savedIngredients`.

## Goals / Non-Goals

**Goals:**
- One rule for every ingredient: no code path names or treats water specially, apart from the
  upgrade that converts old data and the data listing Water as a starting saved ingredient.
- Hydration calculated in one place.
- Every weight identical before and after the upgrade.

**Non-Goals:**
- Preparing for a Flour tick. A later change can add a fourth kind through the
  same upgrade mechanism.

## Decisions

**One `IngredientKind` (percentage, liquid, count) instead of separate flags.** Liquid sits in the same
choice as Count because an ingredient is exactly one of them, so "a liquid count" can't be chosen.
`EditedIngredient`, `ConvertedRow` and `SavedIngredient` hold a kind in place of `isCount`. A separate
Liquid checkbox was rejected: it would need hiding for counts and restoring afterwards, and takes more
space. As today, a row keeps both its percentage and its count while editing, so changing back restores
them, and only the chosen kind is applied.

**`Ingredient` keeps its nullable `count` and gains `isLiquid`.** A recipe's ingredient already holds its
measure in `percentage` or `count`, so a full kind field would repeat what `count` says. Its `kind` is
derived from the two, and `init` refuses a liquid with a count. Older stored ingredients decode as not
liquid without any extra step.

**Saved ingredients are edited in place, with shared controls.** The kind choice and "Grams each" row
become one composable, built from `CountChoice` (generalised to the three kinds) and `GramsEachRow`, and
used by the recipe edit row, Convert's row labels and the saved ingredients screen. A single repository
action replaces a saved ingredient by name. A change writes at once, since this screen has no draft to
apply; only usable "Grams each" text is written, and the field keeps the typed text locally. Grams each
stays stored while the kind is another one, so changing back restores it.

**Remove the stored water field; hydration is calculated.** `DoughRecipe.hydration` becomes a property
over the ingredients, and `DoughAmounts.water` goes. Keeping a renamed field beside the list would
keep the special case this change exists to remove.

**Hydration is whole hundredths, not a `Percentage`.** The total can legitimately pass 200%, which
`Percentage` refuses, and a total has no typed precision. It is calculated on `List<Ingredient>`, so
the recipe and the edit draft share one rule. The rounding text moves out of `Percentage` into a
function of hundredths that both use.

**Liquids-first is applied where lists are produced.** A stable partition runs in
`IngredientListEditor.toIngredients` and `ConversionDraft.preview`, the only producers of a new
ingredient order. The stored list is then always in display order, so all readers just use it,
including the index-based updates in the ingredients card and the zip with amounts in the weights
table. Sorting only at display time was rejected because each reader would need to repeat it and
indexes would no longer line up. The upgrade produces this order naturally: Water is first and is the
only liquid.

**A new `upgradeToLiquidIngredients` writes the shape directly.** Like the earlier upgrades, it works
on JSON, so later class changes can't alter its result. For the calculator's recipe and every saved
recipe, it removes `hydration` and puts `{name: "Water", percentage: <old>, isLiquid: true}` first,
unless the old value is 0.

Two cases follow from defaults not being stored:
- A missing `currentRecipe` is written out as the old default (4 × 250 g, Water 62%), because the new
  default is flour only and would otherwise silently replace it.
- A missing `savedIngredients` stays missing, because the new default already includes Water. A stored
  list gets Water appended. It can't already hold one, because the name was reserved.

The same upgrade turns each stored saved ingredient's `isCount` into its `kind`.

**Reserved names give their own messages.** "Water" leaves the list, so the one shared message no
longer fits. "Flour" and "Hydration" each state their own reason.

**`ConversionDraft` loses `water` and `waterError`.** Totals fold over the rows alone, and Water is
typed in a row like any other ingredient.

**`amounts()` sums hundredths as integers before dividing.** The sum then doesn't depend on ingredient
order, which the upgrade changes.

## Risks / Trade-offs

- **An upgrade mistake silently loses water.** Unknown JSON keys are ignored on read, so a missed
  `hydration` key would drop water without an error and change every weight. → Tests read literal old
  JSON and assert exact weights, across versions 1, 2 and 3.
- **Very small weight differences from the new sum.** Calculating the fraction differently could
  change a double's last digits. → Displayed weights are rounded, and the upgrade tests assert the
  displayed rows match.
- **Existing liquids aren't marked.** A beer dough reads "0% hydration", or too low, until Beer is set
  to Liquid in that recipe, and a saved Beer stays "%" until changed on its screen. The app can't infer
  this without a name rule.
- **A longer saved ingredients list.** Each entry becomes taller with its controls. Accepted, as the
  list is short and scrolls.
- **Downgrading isn't possible.** An older build refuses the newer format, as it does for any format
  upgrade. Not relevant to a single-user app that only moves forward.
