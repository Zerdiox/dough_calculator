## MODIFIED Requirements

### Requirement: What a saved recipe holds
A saved recipe SHALL hold a name, a number of portions, a portion weight, its own list of ingredients
with their names, order, kinds, percentages and precision, or counts and grams each,
and its note. A saved recipe's ingredients SHALL belong to that recipe only: changing the saved
ingredients or another recipe SHALL NOT change them.

#### Scenario: Recipe keeps its portions
- **WHEN** the user saves a recipe with 6 portions of 280 g and opens it later
- **THEN** it opens with 6 portions of 280 g

#### Scenario: Recipe keeps its ingredients
- **WHEN** the user saves a recipe with Salt 3.0% and Honey 2.50%, in that order, and loads it later
- **THEN** the calculator shows Salt 3.0% then Honey 2.50%, with steps of 0.1 and 0.01

#### Scenario: Recipe keeps its liquids
- **WHEN** the user saves a recipe with Beer 65% set to Liquid and loads it later
- **THEN** Beer is still liquid and the calculator shows Hydration at 65%

#### Scenario: Recipe keeps its counts
- **WHEN** the user saves a recipe with Eggs at 2 with 50 g each for 4 portions and loads it later
- **THEN** the calculator shows Eggs at 2 with 50 g each

#### Scenario: Recipe saved before counts existed
- **WHEN** a recipe saved before this update has Salt 3%
- **THEN** Salt is still a percentage ingredient at 3% with the same weight

#### Scenario: Recipe keeps its note
- **WHEN** the user saves a recipe with a note containing a bullet list and numbered steps and opens
  it later
- **THEN** its full recipe shows the same note with the same lists

#### Scenario: Recipe saved before notes existed
- **WHEN** the app is updated and a recipe was saved before notes existed
- **THEN** the recipe has no note and all its other values and its weights are unchanged

#### Scenario: Undo brings back the note
- **WHEN** the user deletes a recipe with a note and taps Undo
- **THEN** the recipe is back with its note

### Requirement: Saved recipes list
The user SHALL reach the saved recipes from the calculator. The list SHALL show the recipes in the
order they were saved, oldest first. Each entry SHALL show the recipe's name and a summary reading
"<portions> × <weight> g · <hydration>% hydration".
Back SHALL return to the calculator.

#### Scenario: Entry summary
- **WHEN** "Friday night" has 4 portions of 250 g and Water 62% as a liquid
- **THEN** its entry shows "Friday night" and "4 × 250 g · 62% hydration"

#### Scenario: Summary counts every liquid
- **WHEN** "Beer dough" has 4 portions of 250 g, Water 25% and Beer 40%, both liquids
- **THEN** its entry shows "Beer dough" and "4 × 250 g · 65% hydration"

#### Scenario: Newest recipe last
- **WHEN** the user saves "A" and then "B"
- **THEN** the list shows "A" above "B"

### Requirement: Load a saved recipe into the calculator
Each saved recipe SHALL offer to edit it in the calculator. Doing so SHALL replace all of the
calculator's values (portions, portion weight, the ingredients and the note) with the recipe's,
without asking, and return to the calculator, which then remembers which recipe it was loaded from.

#### Scenario: Load into the calculator
- **WHEN** the user chooses to edit "Friday night" in the calculator
- **THEN** the calculator shows the portions, portion weight, ingredients and note of "Friday night"

#### Scenario: Ingredients are replaced, not merged
- **WHEN** the calculator has Honey and the user loads "Friday night", which has Salt and Yeast
- **THEN** the calculator has Salt and Yeast and no Honey

#### Scenario: Note is replaced
- **WHEN** the calculator has a note and the user loads a recipe with no note
- **THEN** the Notes field is empty

### Requirement: Recipes survive app updates
Saved recipes SHALL NOT be lost or altered by an app update. After an update, every saved recipe
SHALL keep its name, portions, portion weight, ingredients and percentages, and SHALL produce the same
ingredient weights as before the update.

A recipe saved before ingredients could be named SHALL, after the update, have Salt, Yeast and Olive
oil as its ingredients in that order, each with its old percentage, leaving out any that were at 0%.
Salt and Olive oil SHALL have a step of 0.1, Yeast a step of 0.01 and water a step of 1, or a finer
step where the old value needs it, so no value changes.

A recipe saved when water was a fixed part of every recipe SHALL, after the update, have Water as its
first ingredient, as a liquid, with its old percentage and step, and no other ingredient liquid.
Water at 0% SHALL be left out. The calculator's remembered values SHALL be updated the same way. A
calculator never changed from the old default SHALL keep showing 4 portions of 250 g with Water 62%.

#### Scenario: Recipe after an update
- **WHEN** the app is updated to a new version
- **THEN** every recipe saved before the update is still in the list
- **AND** each shows the same ingredient weights as before

#### Scenario: Fixed ingredients become named ingredients
- **WHEN** a recipe saved before ingredients could be named has 3% salt, 0.2% yeast and 0% olive oil
- **THEN** after the update it has Salt 3.0% and Yeast 0.20%, in that order, and no Olive oil
- **AND** its weights are the same as before the update

#### Scenario: Water becomes an ingredient
- **WHEN** a recipe has 62.5% water, Salt 3% and Yeast 0.2% before the update
- **THEN** after the update it has Water 62.5% as a liquid with a step of 0.1, then Salt and Yeast
- **AND** its entry reads "62.5% hydration"
- **AND** its weights are the same as before the update

#### Scenario: Water at 0% is left out
- **WHEN** a recipe has 0% water and Beer 65% before the update
- **THEN** after the update it has Beer 65%, not liquid, and no Water
- **AND** its entry reads "0% hydration" until the user sets Beer to Liquid
- **AND** its weights are the same as before the update

#### Scenario: Unchanged calculator after the update
- **WHEN** the calculator was never changed from the old default and the app is updated
- **THEN** it shows 4 portions of 250 g with Water 62%
