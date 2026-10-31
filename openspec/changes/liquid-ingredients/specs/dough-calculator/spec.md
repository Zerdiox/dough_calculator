## ADDED Requirements

### Requirement: Hydration row
The top row of the Ingredients card SHALL read "Hydration" with the recipe's hydration as a
percentage, and SHALL have no controls. It SHALL always be shown, while editing too and when there
are no ingredients. It SHALL follow every change to a liquid's percentage and, while editing, every
change to the ingredient list. Liquid ingredients SHALL be listed directly under it, followed by all
others.

#### Scenario: Hydration with liquids
- **WHEN** the recipe has Water 25%, Beer 40% and Salt 3%, with Water and Beer liquid
- **THEN** the card reads "Hydration", "65%", then Water, Beer and Salt, in that order
- **AND** the Hydration row has no field or − and + buttons

#### Scenario: No ingredients
- **WHEN** the recipe has no ingredients
- **THEN** the card reads "Hydration" and "0%"

#### Scenario: Follows a liquid's percentage
- **WHEN** Water is liquid at 60% and the user taps + next to Water
- **THEN** Hydration reads "61%"

#### Scenario: Follows the choice while editing
- **WHEN** Beer is at 40%, and while editing the user changes it to "Liquid"
- **THEN** Hydration goes up by 40 at once

### Requirement: Percentage controls
For every percentage ingredient, the calculator SHALL show its name, a field holding its percentage
followed by "%", − and + buttons on either side of the field, and the step under the field as "± 1",
"± 0.1" or "± 0.01". The field SHALL show the value with as many decimals as its precision. The user
SHALL be able to type a new value, which sets the precision. The − and + buttons SHALL move one step
and SHALL keep the precision. The − button SHALL be disabled at 0% and the + button at 200%.

While the field holds something that is not a number from 0 to 200, the field SHALL say "Enter 0
to 200", and the weights SHALL keep using the last accepted value. Leaving the field SHALL show the
last accepted value again.

#### Scenario: Control uses the ingredient's name
- **WHEN** the calculator shows Water at 62% with a step of 1
- **THEN** its control reads "Water", "62" and "± 1"

#### Scenario: Buttons move one step
- **WHEN** Salt is 3.0% with a step of 0.1 and the user taps + next to Salt
- **THEN** Salt becomes 3.1%

#### Scenario: Field keeps its decimals at zero
- **WHEN** Yeast is 0.01% with a step of 0.01 and the user taps −
- **THEN** the field shows "0.00" and the − button is disabled

#### Scenario: Above 100% is accepted
- **WHEN** Water is 100% with a step of 1 and the user taps +
- **THEN** Water becomes 101%

#### Scenario: Buttons stop at the range ends
- **WHEN** an ingredient is at 200%
- **THEN** the + button next to it is disabled

#### Scenario: Not a number
- **WHEN** the user types "abc" or 250 for Salt
- **THEN** the field says "Enter 0 to 200"
- **AND** the weights table still uses the previous value for Salt

## REMOVED Requirements

### Requirement: Ingredient percentage controls
**Reason**: It labelled water's control "Hydration". Water is now an ordinary ingredient and Hydration
is a calculated total.
**Migration**: Replaced by "Percentage controls" and "Hydration row".

## MODIFIED Requirements

### Requirement: Calculator screen
The app SHALL open on the dough calculator. It SHALL show the portions, the portion weight, the
Hydration row, a control for each of the recipe's ingredients, an Edit button for the ingredients,
the weights table for the current values, and a "Start kneading" button. Next to saving and the saved
recipes, it SHALL offer a way to open the Convert screen.

#### Scenario: App opens on the calculator
- **WHEN** the user opens the app
- **THEN** the dough calculator is shown with the weights table for its current values

#### Scenario: Convert is offered
- **WHEN** the calculator is shown and its ingredients are not being edited
- **THEN** the user can open the Convert screen from it

### Requirement: Default values
On first use the calculator SHALL show 4 portions of 250 g and no ingredients.

#### Scenario: First launch
- **WHEN** the app is opened for the first time
- **THEN** the calculator shows 4 portions of 250 g, Hydration at 0% and no ingredients

### Requirement: Remembered values
The calculator SHALL remember its portions, portion weight and ingredients with their names, order,
kinds, percentages and precision, or counts and grams each, when the app is closed, and
show them again the next time it opens.

#### Scenario: Values survive a restart
- **WHEN** the user sets 6 portions, Water to 68% and adds Honey at 2.5%, then closes and reopens the
  app
- **THEN** the calculator shows 6 portions, Water at 68% and Honey at 2.5% with a step of 0.1

#### Scenario: Liquid survives a restart
- **WHEN** the user sets Beer to "Liquid", then closes and reopens the app
- **THEN** Beer is still liquid

#### Scenario: Counts survive a restart
- **WHEN** the user sets Eggs to 2 with 50 g each, then closes and reopens the app
- **THEN** the calculator shows Eggs at 2 with 50 g each

### Requirement: Edit the ingredients
The Ingredients card SHALL have an Edit button. While editing:

- The percentage and count controls SHALL be hidden. The Hydration row SHALL stay at the top.
- Every ingredient SHALL be shown as a name field, a handle to drag it to another place, a remove
  button, and a choice between "%", "Liquid" and "Count".
- An ingredient set to "Count" SHALL also show an optional "Grams each" field, which SHALL take grams
  with up to two decimals, a comma counting as a decimal point. Left blank or at 0, the ingredient
  SHALL have no grams each. Anything else SHALL make the field say "Enter grams".
- An "Add ingredient" button SHALL add a row with an empty name field, set to "%", at the end.
- Changing a row's choice SHALL NOT move the row while editing.
- The Edit button SHALL read "Done". Done SHALL be disabled while any name is blank or breaks the
  ingredient name rules, or a "Grams each" field shows a message. A name that breaks the rules SHALL
  show why under its field: "Flour is part of every recipe already", "Hydration is worked out from
  the liquids", or ""<name>" is already in this recipe".
- Saving, opening the saved recipes, opening Convert and "Start kneading" SHALL be unavailable.
- Back SHALL do the same as Done while Done is enabled, and nothing otherwise.

Done SHALL apply the names, order, kinds, grams each, removals and additions to the
calculator, with liquid ingredients placed before all others and each group in the order left while
editing. An ingredient added while editing SHALL start at 0% with a step of 1. An ingredient changed
to "Count" SHALL start at 0 and an ingredient changed from "Count" to "%" or "Liquid" SHALL start at
0% with a step of 1, except that changing back during the same edit SHALL restore what it held.
Changing between "%" and "Liquid" SHALL keep the percentage and its step. Changes made
while editing SHALL NOT change any saved recipe until the recipe is saved. If the app is closed while
editing, the calculator SHALL reopen with its values from before editing began.

#### Scenario: Rename an ingredient
- **WHEN** the user taps Edit, changes "Salt" to "Sea salt" and taps Done
- **THEN** the calculator and the weights table show "Sea salt" with Salt's percentage

#### Scenario: Reorder by dragging
- **WHEN** the recipe has Salt then Yeast, and while editing the user drags Yeast above Salt and
  taps Done
- **THEN** the calculator and the weights table list Yeast before Salt

#### Scenario: Choose Liquid
- **WHEN** the recipe has Salt then Beer, and while editing the user changes Beer to "Liquid"
- **THEN** Beer stays below Salt while editing
- **AND** after Done the calculator and the weights table list Beer before Salt

#### Scenario: Dragging across the groups
- **WHEN** Water is liquid and Salt is not, and while editing the user drags Salt above Water and
  taps Done
- **THEN** the calculator and the weights table list Water before Salt

#### Scenario: Remove water
- **WHEN** the recipe has Water and Beer, and the user removes Water and taps Done
- **THEN** the calculator and the weights table no longer show Water
- **AND** Hydration is Beer's percentage

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
- **WHEN** Beer is liquid at 40%, and while editing the user chooses "Count", then "Liquid", and taps
  Done
- **THEN** Beer is still liquid at 40%

#### Scenario: Between percentage and liquid
- **WHEN** Beer is at 40% with a step of 1, and while editing the user changes it to "Liquid" and taps
  Done
- **THEN** Beer is liquid at 40% with a step of 1

#### Scenario: A count is not liquid
- **WHEN** Beer is liquid at 40%, and while editing the user chooses "Count" and taps Done
- **THEN** Beer no longer counts towards Hydration

#### Scenario: Grams each that can't be used
- **WHEN** the user types "abc" for "Grams each"
- **THEN** the field says "Enter grams" and Done is disabled

#### Scenario: Duplicate name blocks Done
- **WHEN** the recipe has Salt and the user adds a row named "salt"
- **THEN** the row says ""Salt" is already in this recipe" and Done is disabled

#### Scenario: Reserved name blocks Done
- **WHEN** the user adds a row named "Hydration"
- **THEN** the row says "Hydration is worked out from the liquids" and Done is disabled

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

### Requirement: Remove an ingredient while editing
Removing an ingredient while editing SHALL take it out of the list at once and show a message
"Removed "<name>"" with an Undo action. Undo SHALL put the ingredient back in its place with its
name, kind, percentage and precision, or count and grams each. Undo SHALL stay offered
for about ten seconds, and SHALL end when the user taps Done or removes another ingredient.

#### Scenario: Undo a removal
- **WHEN** while editing the user removes Salt, the middle of three ingredients, and taps Undo
- **THEN** Salt is back in the middle with its percentage and step

#### Scenario: Undo keeps the kind
- **WHEN** while editing the user removes Beer, which is liquid, and taps Undo
- **THEN** Beer is back and still liquid

#### Scenario: Removal applied on Done
- **WHEN** while editing the user removes Salt and taps Done
- **THEN** the calculator and the weights table no longer show Salt

### Requirement: Pick a saved ingredient when adding
While the name field of an ingredient added during this edit is focused, a dropdown SHALL list the
saved ingredients that are not yet in the recipe, alphabetically, keeping only those whose name
contains the typed text, ignoring upper and lower case. Picking one SHALL fill in its name and set
the row to the saved ingredient's kind and grams each. When nothing matches, the
dropdown SHALL say so, and the typed name SHALL be used as a new ingredient. Rows that were already
in the recipe when editing began SHALL NOT show the dropdown.

#### Scenario: Pick from the dropdown
- **WHEN** Yeast is a saved ingredient not in the recipe, and the user adds a row and picks Yeast
- **THEN** the row's name is "Yeast"

#### Scenario: Pick a saved liquid
- **WHEN** Water is saved as a liquid, and the user adds a row and picks Water
- **THEN** the row's name is "Water" and it is set to "Liquid"

#### Scenario: Pick a saved count
- **WHEN** Eggs is saved as a count with 50 g each, and the user adds a row and picks Eggs
- **THEN** the row's name is "Eggs", it is set to "Count" and "Grams each" holds 50

#### Scenario: Already in the recipe
- **WHEN** the recipe has Salt and the user adds a row
- **THEN** the dropdown does not offer Salt

#### Scenario: Filter by typing
- **WHEN** the saved ingredients are Olive oil, Salt and Yeast and the user types "o"
- **THEN** the dropdown offers Olive oil only
