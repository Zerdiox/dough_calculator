# Spec Delta

## MODIFIED Requirements

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
