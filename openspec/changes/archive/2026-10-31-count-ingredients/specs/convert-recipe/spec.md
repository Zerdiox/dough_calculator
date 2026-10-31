# Spec Delta

## ADDED Requirements

### Requirement: Count rows on Convert
Each other-ingredient row on Convert SHALL offer a choice between "Grams" and "Count", starting at
"Grams". Changing the choice SHALL keep what is typed. A "Count" row's amount field SHALL show no "g",
SHALL take a whole or half number up to 100, a comma counting as a decimal point, and SHALL otherwise
say "Enter 0 to 100, in halves". A "Count" row SHALL also show an optional "Grams each" field, with
the same rules as other weights on Convert; left blank or at 0, the ingredient SHALL have no grams
each. The converted recipe SHALL have the ingredient as a count, with the typed amount for the
portions on the screen. A "Count" row whose amount is blank or 0 SHALL be left out of the converted
recipe. A row with a message in either field SHALL be left out of the weights table. Picking a saved
ingredient SHALL set the row to "Count" with its grams each, or to "Grams", as saved, and SHALL keep
the amount typed in the row.

#### Scenario: Pick a saved count
- **WHEN** Eggs is saved as a count with 50 g each, and the user picks Eggs on a row holding 2
- **THEN** the row is set to "Count" with amount 2 and 50 g each

#### Scenario: Eggs in a book recipe
- **WHEN** the user types Flour 500 and Water 300, adds Eggs set to "Count" with amount 2 and 50 g
  each, with 4 portions
- **THEN** the weights table reads "4 × 225 g" with Flour at 100%, Water at 60% and Eggs "× 2" and
  "100 g"

#### Scenario: Count without grams each
- **WHEN** the user types Flour 500 and Water 300, adds Eggs set to "Count" with amount 2 and no
  grams each, with 4 portions
- **THEN** the weights table reads "4 × 200 g" with Eggs "× 2" and no weight

#### Scenario: Changing the portions keeps the typed amount
- **WHEN** the row Eggs has amount 2 and the user changes Convert's portions from 4 to 6
- **THEN** the weights table still shows Eggs "× 2"

#### Scenario: Amount that can't be used
- **WHEN** the user types 1.3 for the amount of a "Count" row
- **THEN** the field says "Enter 0 to 100, in halves"
- **AND** the weights table has no row for it
- **AND** both buttons are disabled

## MODIFIED Requirements

### Requirement: Other ingredients on Convert
"Add ingredient" SHALL add a row with an empty name field and an empty grams field at the end. Each
row SHALL have a remove button that takes it out at once and shows a message "Removed "<name>""
with an Undo action, using "new ingredient" for a row without a name. Undo SHALL put the row back in
its place with its name, choice of "Grams" or "Count", amount and grams each. Undo SHALL stay offered
for about ten seconds, and SHALL end when the user removes another row, saves, uses the recipe in the
calculator, or leaves the Convert screen. Names SHALL follow the ingredient name
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
Water's percentage and the percentage of every other row set to "Grams" SHALL be its weight divided
by the flour weight, times 100, rounded half up to two decimals. Its precision SHALL be the fewest
decimals the rounded value needs, from none to two. A row whose grams field is blank or 0 SHALL be
left out of the converted recipe. An ingredient that rounds to 0% SHALL be kept at 0%. A percentage
above 200% SHALL make that row's grams field say "Over 200% of the flour".

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
total of all weights, including count rows' amounts multiplied by their grams each, divided by the
portions, rounded down to whole grams. A count row without grams each SHALL add nothing. A portion
weight under 5 g SHALL make the screen say "Portions under 5 g".

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
