# Spec Delta

## ADDED Requirements

### Requirement: Count ingredient controls
For a count ingredient, the calculator SHALL show its name, a field holding its amount for the
current portions with no unit after it, − and + buttons on either side of the field, and "± 0.5"
under the field. The user SHALL be able to type a whole or half number. The − and + buttons SHALL
move 0.5. The − button SHALL be disabled at 0 and the + button at 100. Changing the amount SHALL set
it for the current portions.

While the field holds something else, it SHALL say "Enter 0 to 100, in halves", and the recipe SHALL
keep using the last accepted amount. Leaving the field SHALL show the last accepted amount again.
A comma SHALL count as a decimal point.

#### Scenario: Amount follows the portions
- **WHEN** Eggs shows 2 at 4 portions and the user sets 3 portions
- **THEN** the Eggs field shows 1.5

#### Scenario: Changing the amount
- **WHEN** Eggs shows 1.5 at 3 portions, the user taps + and then sets 6 portions
- **THEN** Eggs shows 4

#### Scenario: Not a half
- **WHEN** the user types 1.3 for Eggs
- **THEN** the field says "Enter 0 to 100, in halves"
- **AND** the weights table still uses the previous amount

## MODIFIED Requirements

### Requirement: Remembered values
The calculator SHALL remember its portions, portion weight, water's percentage, and its other
ingredients with their names, order, kinds, percentages and precision, or counts and grams each, when
the app is closed, and show them again the next time it opens.

#### Scenario: Values survive a restart
- **WHEN** the user sets 6 portions, 68% water and adds Honey at 2.5%, then closes and reopens the
  app
- **THEN** the calculator shows 6 portions, 68% water and Honey at 2.5% with a step of 0.1

#### Scenario: Counts survive a restart
- **WHEN** the user sets Eggs to 2 with 50 g each, then closes and reopens the app
- **THEN** the calculator shows Eggs at 2 with 50 g each

### Requirement: Edit the ingredients
The Ingredients card SHALL have an Edit button. While editing:

- The percentage and count controls SHALL be hidden. Hydration SHALL be shown as part of every recipe
  and SHALL NOT be renamed, moved or removed.
- Every other ingredient SHALL be shown as a name field, a handle to drag it to another place, a
  remove button, and a choice between "%" and "Count".
- An ingredient set to "Count" SHALL also show an optional "Grams each" field, which SHALL take grams
  with up to two decimals, a comma counting as a decimal point. Left blank or at 0, the ingredient
  SHALL have no grams each. Anything else SHALL make the field say "Enter grams".
- An "Add ingredient" button SHALL add a row with an empty name field, set to "%", at the end.
- The Edit button SHALL read "Done". Done SHALL be disabled while any name is blank or breaks the
  ingredient name rules, or a "Grams each" field shows a message. A name that breaks the rules SHALL
  show why under its field: "Flour and water are part of every recipe already", or ""<name>" is
  already in this recipe".
- Saving, opening the saved recipes, opening Convert and "Start kneading" SHALL be unavailable.
- Back SHALL do the same as Done while Done is enabled, and nothing otherwise.

Done SHALL apply the names, order, kinds, grams each, removals and additions to the calculator. An
ingredient added while editing SHALL start at 0% with a step of 1. An ingredient changed to "Count"
SHALL start at 0 and an ingredient changed to "%" SHALL start at 0% with a step of 1, except that
changing back during the same edit SHALL restore what it held. Changes made while editing SHALL NOT
change any saved recipe until the recipe is saved. If the app is closed while editing, the calculator
SHALL reopen with its values from before editing began.

#### Scenario: Rename an ingredient
- **WHEN** the user taps Edit, changes "Salt" to "Sea salt" and taps Done
- **THEN** the calculator and the weights table show "Sea salt" with Salt's percentage

#### Scenario: Reorder by dragging
- **WHEN** the recipe has Salt then Yeast, and while editing the user drags Yeast above Salt and
  taps Done
- **THEN** the calculator and the weights table list Yeast before Salt

#### Scenario: Add an ingredient
- **WHEN** the user taps Edit, Add ingredient, types "Honey" and taps Done
- **THEN** Honey is in the calculator at 0% with "± 1" under its field
- **AND** the weights table has no Honey row until its percentage is above 0%

#### Scenario: Make an ingredient a count
- **WHEN** the user taps Edit, Add ingredient, types "Eggs", chooses "Count", types 50 for "Grams
  each" and taps Done
- **THEN** Eggs is in the calculator at 0 with "± 0.5" under its field
- **AND** the weights table has no Eggs row until its amount is above 0

#### Scenario: Changing the kind back during an edit
- **WHEN** Salt is at 3%, and while editing the user chooses "Count", then "%", and taps Done
- **THEN** Salt is still at 3%

#### Scenario: Grams each that can't be used
- **WHEN** the user types "abc" for "Grams each"
- **THEN** the field says "Enter grams" and Done is disabled

#### Scenario: Duplicate name blocks Done
- **WHEN** the recipe has Salt and the user adds a row named "salt"
- **THEN** the row says ""Salt" is already in this recipe" and Done is disabled

#### Scenario: Blank name blocks Done
- **WHEN** the user adds a row and leaves its name empty
- **THEN** Done is disabled and no message is shown

#### Scenario: Not saved until the recipe is saved
- **WHEN** the calculator was loaded from "Friday night", the user removes Yeast and taps Done
- **THEN** "Friday night" in the saved recipes still has Yeast

#### Scenario: Start kneading uses applied changes
- **WHEN** the user adds Honey at 2% after tapping Done, then taps "Start kneading"
- **THEN** the full recipe shows Honey

#### Scenario: Convert unavailable while editing
- **WHEN** the user taps Edit
- **THEN** Convert cannot be opened until editing ends

### Requirement: Pick a saved ingredient when adding
While the name field of an ingredient added during this edit is focused, a dropdown SHALL list the
saved ingredients that are not yet in the recipe, alphabetically, keeping only those whose name
contains the typed text, ignoring upper and lower case. Picking one SHALL fill in its name and set
the row to the saved ingredient's kind and grams each. When nothing matches, the dropdown SHALL say
so, and the typed name SHALL be used as a new ingredient. Rows that were already in the recipe when
editing began SHALL NOT show the dropdown.

#### Scenario: Pick from the dropdown
- **WHEN** Yeast is a saved ingredient not in the recipe, and the user adds a row and picks Yeast
- **THEN** the row's name is "Yeast"

#### Scenario: Pick a saved count
- **WHEN** Eggs is saved as a count with 50 g each, and the user adds a row and picks Eggs
- **THEN** the row's name is "Eggs", it is set to "Count" and "Grams each" holds 50

#### Scenario: Already in the recipe
- **WHEN** the recipe has Salt and the user adds a row
- **THEN** the dropdown does not offer Salt

#### Scenario: Filter by typing
- **WHEN** the saved ingredients are Olive oil, Salt and Yeast and the user types "o"
- **THEN** the dropdown offers Olive oil only

### Requirement: Remove an ingredient while editing
Removing an ingredient while editing SHALL take it out of the list at once and show a message
"Removed "<name>"" with an Undo action. Undo SHALL put the ingredient back in its place with its
name, kind, percentage and precision, or count and grams each. Undo SHALL stay offered for about ten
seconds, and SHALL end when the user taps Done or removes another ingredient.

#### Scenario: Undo a removal
- **WHEN** while editing the user removes Salt, the middle of three ingredients, and taps Undo
- **THEN** Salt is back in the middle with its percentage and step

#### Scenario: Removal applied on Done
- **WHEN** while editing the user removes Salt and taps Done
- **THEN** the calculator and the weights table no longer show Salt
