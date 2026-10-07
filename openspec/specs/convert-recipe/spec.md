# convert-recipe Specification

## Purpose

Lets the user turn a recipe written in grams, as found in books and on websites, into a recipe in
baker's percentages, and then use it on the calculator or keep it as a saved recipe.

## Requirements

### Requirement: Convert screen
The calculator SHALL offer a way to open the Convert screen, titled "Convert recipe". The screen
SHALL show a grams field for Flour and for Water, the rows for other ingredients with an "Add
ingredient" button, the portions, the weights table for the converted recipe, an "Add more"
checkbox, a "Save as recipe" button and a "Use in calculator" button. Back SHALL return to the
calculator without changing anything.

#### Scenario: Open the Convert screen
- **WHEN** the user opens Convert from the calculator
- **THEN** the screen "Convert recipe" shows empty Flour and Water fields, no other ingredients, the
  portions and both buttons

#### Scenario: Back changes nothing
- **WHEN** the user types a recipe on Convert and goes back
- **THEN** the calculator shows the same values as before
- **AND** no recipe is saved

### Requirement: Weights in grams
Every weight on the Convert screen SHALL be typed in grams, with up to two decimals. A comma SHALL
count as a decimal point. Each field SHALL show "g" after the value. A field holding something else
SHALL say "Enter grams". Flour SHALL be more than 0 g; at 0 g its field SHALL say "More than 0 g".
A blank Water field SHALL count as 0 g.

#### Scenario: Decimal grams
- **WHEN** the user types "1,5" for Yeast
- **THEN** Yeast counts as 1.5 g

#### Scenario: Not a weight
- **WHEN** the user types "abc" or "1.255" for Salt
- **THEN** the Salt field says "Enter grams"

#### Scenario: No flour
- **WHEN** the user types 0 for Flour
- **THEN** the Flour field says "More than 0 g"

### Requirement: Other ingredients on Convert
"Add ingredient" SHALL add a row with an empty name field and an empty grams field at the end. Each
row SHALL have a remove button that takes it out at once and shows a message "Removed "<name>""
with an Undo action, using "new ingredient" for a row without a name. Undo SHALL put the row back in
its place with its name and grams. Undo SHALL stay offered for about ten seconds, and SHALL end when
the user removes another row, saves, uses the recipe in the calculator, or leaves the Convert
screen. Names SHALL follow the ingredient name
rules, and a name that breaks them SHALL show why under its field, in the same words as while
editing the calculator's ingredients. While a name field is focused, a dropdown SHALL offer the
saved ingredients the same way as when adding an ingredient on the calculator. The converted recipe
SHALL keep the rows in the order shown.

#### Scenario: Add a named ingredient
- **WHEN** the user taps "Add ingredient", picks Salt from the dropdown and types 15
- **THEN** the converted recipe has Salt

#### Scenario: Name already used
- **WHEN** the rows have Salt and the user names another row "salt"
- **THEN** the row says ""Salt" is already in this recipe"

#### Scenario: Remove a row
- **WHEN** the user removes the Salt row
- **THEN** the converted recipe no longer has Salt
- **AND** a message says "Removed "Salt"" with an Undo action

#### Scenario: Undo a removal
- **WHEN** the user removes Salt, the middle of three rows, and taps Undo
- **THEN** Salt is back in the middle with its name and grams

### Requirement: Percentages from grams
Water's and every other ingredient's percentage SHALL be its weight divided by the flour weight,
times 100, rounded half up to two decimals. Its precision SHALL be the fewest decimals the rounded
value needs, from none to two. A row whose grams field is blank or 0 SHALL be left out of the
converted recipe. An ingredient that rounds to 0% SHALL be kept at 0%. A percentage above 200%
SHALL make that row's grams field say "Over 200% of the flour".

#### Scenario: Book recipe
- **WHEN** the user types Flour 500, Water 325, Salt 15 and Yeast 1
- **THEN** the converted recipe has water at 65% with a step of 1, Salt at 3% with a step of 1, and
  Yeast at 0.2% with a step of 0.1

#### Scenario: Rounded to two decimals
- **WHEN** the user types Flour 600 and Yeast 1
- **THEN** Yeast is 0.17% with a step of 0.01

#### Scenario: Empty row left out
- **WHEN** a row named Honey has no grams
- **THEN** the converted recipe has no Honey

#### Scenario: Too much of an ingredient
- **WHEN** the user types Flour 100 and Water 250
- **THEN** the Water field says "Over 200% of the flour"

### Requirement: Portions on Convert
The Convert screen SHALL set the number of portions with − and + buttons, within the same limits as
on the calculator, starting at the calculator's number of portions. The portion weight SHALL be the
total of all weights divided by the portions, rounded down to whole grams. A portion weight under
5 g SHALL make the screen say "Portions under 5 g".

#### Scenario: Starts at the calculator's portions
- **WHEN** the calculator has 6 portions and the user opens Convert
- **THEN** Convert shows 6 portions

#### Scenario: Portion weight rounded down
- **WHEN** the weights add up to 841 g and the portions are 4
- **THEN** the converted recipe has 4 portions of 210 g

#### Scenario: Portions too small
- **WHEN** the weights add up to 18 g and the portions are 4
- **THEN** the screen says "Portions under 5 g"
- **AND** both "Save as recipe" and "Use in calculator" are disabled

### Requirement: Converted recipe preview
While the input can be converted, the Convert screen SHALL show the weights table for the converted
recipe and update it as soon as any input changes. While any field shows a message, the screen
says "Portions under 5 g", a name is blank, or Flour is blank, the weights table SHALL be hidden and both "Save as recipe" and "Use in
calculator" SHALL be disabled.

#### Scenario: Live preview
- **WHEN** the user types Flour 500 and Water 325 with 4 portions
- **THEN** the weights table reads "4 × 206 g" with Flour and Water rows at 100% and 65%

#### Scenario: Unfinished input
- **WHEN** a row has grams but a blank name
- **THEN** no weights table is shown and both buttons are disabled

### Requirement: Use in calculator
"Use in calculator" SHALL replace all of the calculator's values with the converted recipe, without
asking, and return to the calculator. The calculator SHALL then count as not loaded from any saved
recipe, so saving it SHALL offer only saving a new recipe.

#### Scenario: Send to the calculator
- **WHEN** the user converts Flour 500, Water 325, Salt 15 and Yeast 1 with 4 portions and taps
  "Use in calculator"
- **THEN** the calculator shows 4 portions of 210 g, Hydration at 65%, Salt at 3% and Yeast at 0.2%

#### Scenario: Not linked to the loaded recipe
- **WHEN** the calculator was loaded from "Friday night" and the user uses a converted recipe in it
  and then saves
- **THEN** only saving as a new recipe is offered

### Requirement: Save as recipe from Convert
"Save as recipe" SHALL ask for a name in the same dialog as saving a new recipe on the calculator,
with the same rules, and save the converted recipe as a new recipe. It SHALL NOT change the
calculator or which recipe the calculator was loaded from. Cancelling SHALL save nothing.

The "Add more" checkbox SHALL start unticked each time Convert is opened. When it is ticked, saving
SHALL keep the Convert screen open, clear the Flour and Water fields, remove the other ingredient
rows, keep the portions and the tick, and show a message "Saved "<name>"". When it is not ticked, saving SHALL open the new
recipe's full recipe, titled with its name, and Back from there SHALL return to the calculator.

#### Scenario: Save and bake
- **WHEN** "Add more" is unticked and the user saves the converted recipe as "Book Neapolitan"
- **THEN** the full recipe of "Book Neapolitan" opens
- **AND** "Book Neapolitan" is in the saved recipes
- **AND** Back returns to the calculator with its values unchanged

#### Scenario: Save and convert another
- **WHEN** "Add more" is ticked and the user saves the converted recipe as "Book Neapolitan"
- **THEN** Convert stays open with empty Flour and Water fields and no other ingredients
- **AND** a message confirms "Saved "Book Neapolitan""

#### Scenario: Calculator link unchanged
- **WHEN** the calculator was loaded from "Friday night" and the user saves a converted recipe
- **THEN** saving on the calculator still offers to update "Friday night"

### Requirement: Convert input is not remembered
The Convert screen SHALL keep its input while the phone is rotated. It SHALL start empty each time
it is opened, including after the app is closed and opened again.

#### Scenario: Opened again
- **WHEN** the user types a recipe on Convert, goes back and opens Convert again
- **THEN** the Flour and Water fields are empty and there are no other ingredients
