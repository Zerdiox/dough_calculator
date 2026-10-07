# dough-calculator Specification

## Purpose

Lets the user work out a batch of dough by choosing how many portions to make, how heavy each
portion is and the ingredient percentages, and save the result as a recipe.

## Requirements

### Requirement: Calculator screen
The app SHALL open on the dough calculator. It SHALL show the portions, the portion weight, a
control for water's percentage and for each of the recipe's other ingredients, an Edit button for
the ingredients, the weights table for the current values, and a "Start kneading" button. Next to
saving and the saved recipes, it SHALL offer a way to open the Convert screen.

#### Scenario: App opens on the calculator
- **WHEN** the user opens the app
- **THEN** the dough calculator is shown with the weights table for its current values

#### Scenario: Convert is offered
- **WHEN** the calculator is shown and its ingredients are not being edited
- **THEN** the user can open the Convert screen from it

### Requirement: Portions
The user SHALL set the number of portions with − and + buttons, one portion at a time, between 1 and
50. The − button SHALL be disabled at 1 and the + button SHALL be disabled at 50. The screen SHALL
call them "Portions".

#### Scenario: Cannot go below one portion
- **WHEN** the calculator is at 1 portion
- **THEN** the − button is disabled

#### Scenario: Cannot go above fifty portions
- **WHEN** the calculator is at 50 portions
- **THEN** the + button is disabled

### Requirement: Portion weight
The user SHALL set the portion weight by typing it in whole grams into a field, or with − and +
buttons next to it. The portion weight SHALL be at least 5 g, and the field SHALL take up to six
digits. A typed weight SHALL be kept as typed. The − and + buttons SHALL move to the next lower or
higher multiple of 5 g. The − button SHALL be disabled at 5 g. The screen SHALL call it "Portion
weight", show "g" after the value, and show "± 5 g" under the field.

While the field holds something that is not a whole number of at least 5, the field SHALL say "At
least 5 g", and the weights SHALL keep using the last accepted weight. Leaving the field SHALL show
the last accepted weight again.

#### Scenario: Fine-tune the portion weight
- **WHEN** the portion weight is 250 g and the user taps +
- **THEN** the portion weight becomes 255 g

#### Scenario: Typed weight off the 5 g grid
- **WHEN** the user types 252 and then taps +
- **THEN** the portion weight is 252 g after typing and 255 g after tapping +

#### Scenario: Large portion
- **WHEN** the user types 1200
- **THEN** the portion weight is 1200 g and the weights table follows

#### Scenario: Too small
- **WHEN** the user types 3
- **THEN** the field says "At least 5 g"
- **AND** the weights table still uses the previous portion weight

### Requirement: Ingredient percentage controls
For water and every other ingredient, the calculator SHALL show its name, a field holding its
percentage followed by "%", − and + buttons on either side of the field, and the step under the
field as "± 1", "± 0.1" or "± 0.01". Water's control SHALL be labelled "Hydration"; every other
ingredient's control SHALL use the ingredient's name. The field SHALL show the value with as many
decimals as its precision. The user SHALL be able to type a new value, which sets the precision.
The − and + buttons SHALL move one step and SHALL keep the precision. The − button SHALL be
disabled at 0% and the + button at 200%.

While the field holds something that is not a number from 0 to 200, the field SHALL say "Enter 0
to 200", and the weights SHALL keep using the last accepted value. Leaving the field SHALL show the
last accepted value again.

#### Scenario: Water is labelled as hydration
- **WHEN** the calculator shows water at 62%
- **THEN** its control reads "Hydration", "62" and "± 1"

#### Scenario: Buttons move one step
- **WHEN** Salt is 3.0% with a step of 0.1 and the user taps + next to Salt
- **THEN** Salt becomes 3.1%

#### Scenario: Field keeps its decimals at zero
- **WHEN** Yeast is 0.01% with a step of 0.01 and the user taps −
- **THEN** the field shows "0.00" and the − button is disabled

#### Scenario: Above 100% is accepted
- **WHEN** water is 100% with a step of 1 and the user taps +
- **THEN** water becomes 101%

#### Scenario: Buttons stop at the range ends
- **WHEN** an ingredient is at 200%
- **THEN** the + button next to it is disabled

#### Scenario: Not a number
- **WHEN** the user types "abc" or 250 for Salt
- **THEN** the field says "Enter 0 to 200"
- **AND** the weights table still uses the previous value for Salt

### Requirement: Default values
On first use the calculator SHALL show 4 portions of 250 g, water at 62% and no other ingredients.

#### Scenario: First launch
- **WHEN** the app is opened for the first time
- **THEN** the calculator shows 4 portions of 250 g, Hydration at 62% and no other ingredients

### Requirement: Remembered values
The calculator SHALL remember its portions, portion weight, water's percentage, and its other
ingredients with their names, order, percentages and precision when the app is closed, and show
them again the next time it opens.

#### Scenario: Values survive a restart
- **WHEN** the user sets 6 portions, 68% water and adds Honey at 2.5%, then closes and reopens the
  app
- **THEN** the calculator shows 6 portions, 68% water and Honey at 2.5% with a step of 0.1

### Requirement: Save as a new recipe
The user SHALL be able to save the calculator's current values as a new recipe. Saving SHALL ask for
a name; the Save button SHALL stay disabled while the name is blank, and leading and trailing spaces
SHALL be removed. A name already used by another recipe SHALL be allowed. After saving, the app
SHALL confirm with a short message naming the recipe. Cancelling SHALL save nothing.

#### Scenario: Save a recipe
- **WHEN** the user saves the calculator with the name "Friday night"
- **THEN** a recipe named "Friday night" with the calculator's values appears in the saved recipes
- **AND** a message confirms "Saved "Friday night""

#### Scenario: Blank name cannot be saved
- **WHEN** the name field is empty or only spaces
- **THEN** the Save button is disabled

#### Scenario: Duplicate name is allowed
- **WHEN** a recipe named "Friday night" exists and the user saves another with the same name
- **THEN** both recipes are in the saved recipes

### Requirement: Update a loaded recipe
When the calculator's values were loaded from a saved recipe that still exists, saving SHALL offer
two choices: update that recipe with the calculator's current values, keeping its name, or save a
new recipe under a new name. Cancelling the choice SHALL save nothing. After updating, the app SHALL
confirm with a short message naming the recipe. The calculator SHALL keep remembering the recipe it
was loaded from after the app is closed and opened again. After saving as new, the calculator SHALL
count as loaded from the new recipe. When the loaded recipe has since been deleted, saving SHALL
behave as saving a new recipe.

#### Scenario: Update the loaded recipe
- **WHEN** the user loads "Friday night" into the calculator, changes water to 65% and chooses to
  update it
- **THEN** "Friday night" now has 65% water
- **AND** no new recipe is added
- **AND** a message confirms "Updated "Friday night""

#### Scenario: Save the loaded recipe as new
- **WHEN** the user loads "Friday night", changes water to 65% and chooses to save as new with the
  name "Wetter Friday"
- **THEN** "Wetter Friday" is added with 65% water
- **AND** "Friday night" is unchanged

#### Scenario: Loaded recipe was deleted
- **WHEN** the recipe the calculator was loaded from has been deleted and the user saves
- **THEN** only saving as a new recipe is offered

#### Scenario: Loaded recipe remembered after a restart
- **WHEN** the user loads "Friday night", closes the app, opens it again and saves
- **THEN** updating "Friday night" is offered

#### Scenario: Update after saving as new
- **WHEN** the user loads "Friday night", saves as new with the name "Wetter Friday", changes salt to
  2.5% and saves again
- **THEN** updating "Wetter Friday" is offered

### Requirement: Start kneading
The "Start kneading" button SHALL open the full recipe for the calculator's current values, titled
"Full recipe".

#### Scenario: Start kneading from the calculator
- **WHEN** the calculator shows 6 portions of 280 g and the user taps "Start kneading"
- **THEN** the full recipe opens titled "Full recipe" with 6 portions of 280 g and the calculator's
  percentages

### Requirement: Edit the ingredients
The Ingredients card SHALL have an Edit button. While editing:

- The percentage controls SHALL be hidden. Hydration SHALL be shown as part of every recipe and
  SHALL NOT be renamed, moved or removed.
- Every other ingredient SHALL be shown as a name field, a handle to drag it to another place, and
  a remove button.
- An "Add ingredient" button SHALL add a row with an empty name field at the end.
- The Edit button SHALL read "Done". Done SHALL be disabled while any name is blank or breaks the
  ingredient name rules. A name that breaks the rules SHALL show why under its field: "Flour and
  water are part of every recipe already", or ""<name>" is already in this recipe".
- Saving, opening the saved recipes, opening Convert and "Start kneading" SHALL be unavailable.
- Back SHALL do the same as Done while Done is enabled, and nothing otherwise.

Done SHALL apply the names, order, removals and additions to the calculator. An ingredient added
while editing SHALL start at 0% with a step of 1. Changes made while editing SHALL NOT change any
saved recipe until the recipe is saved. If the app is closed while editing, the calculator SHALL
reopen with its values from before editing began.

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

### Requirement: Remove an ingredient while editing
Removing an ingredient while editing SHALL take it out of the list at once and show a message
"Removed "<name>"" with an Undo action. Undo SHALL put the ingredient back in its place with its
name, percentage and precision. Undo SHALL stay offered for about ten seconds, and SHALL end when
the user taps Done or removes another ingredient.

#### Scenario: Undo a removal
- **WHEN** while editing the user removes Salt, the middle of three ingredients, and taps Undo
- **THEN** Salt is back in the middle with its percentage and step

#### Scenario: Removal applied on Done
- **WHEN** while editing the user removes Salt and taps Done
- **THEN** the calculator and the weights table no longer show Salt

### Requirement: Pick a saved ingredient when adding
While the name field of an ingredient added during this edit is focused, a dropdown SHALL list the
saved ingredients that are not yet in the recipe, alphabetically, keeping only those whose name
contains the typed text, ignoring upper and lower case. Picking one SHALL fill in its name. When
nothing matches, the dropdown SHALL say so, and the typed name SHALL be used as a new ingredient.
Rows that were already in the recipe when editing began SHALL NOT show the dropdown.

#### Scenario: Pick from the dropdown
- **WHEN** Yeast is a saved ingredient not in the recipe, and the user adds a row and picks Yeast
- **THEN** the row's name is "Yeast"

#### Scenario: Already in the recipe
- **WHEN** the recipe has Salt and the user adds a row
- **THEN** the dropdown does not offer Salt

#### Scenario: Filter by typing
- **WHEN** the saved ingredients are Olive oil, Salt and Yeast and the user types "o"
- **THEN** the dropdown offers Olive oil only
