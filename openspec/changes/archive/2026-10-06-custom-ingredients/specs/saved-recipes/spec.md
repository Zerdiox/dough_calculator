# Spec Delta

## MODIFIED Requirements

### Requirement: What a saved recipe holds
A saved recipe SHALL hold a name, a number of portions, a portion weight, water's percentage, and
its own list of other ingredients with their names, order, percentages and precision. A saved
recipe's ingredients SHALL belong to that recipe only: changing the saved ingredients or another
recipe SHALL NOT change them.

#### Scenario: Recipe keeps its portions
- **WHEN** the user saves a recipe with 6 portions of 280 g and opens it later
- **THEN** it opens with 6 portions of 280 g

#### Scenario: Recipe keeps its ingredients
- **WHEN** the user saves a recipe with Salt 3.0% and Honey 2.50%, in that order, and loads it later
- **THEN** the calculator shows Salt 3.0% then Honey 2.50%, with steps of 0.1 and 0.01

### Requirement: Load a saved recipe into the calculator
Each saved recipe SHALL offer to edit it in the calculator. Doing so SHALL replace all of the
calculator's values (portions, portion weight, water's percentage and the other ingredients) with
the recipe's, without asking, and return to the calculator, which then remembers which recipe it
was loaded from.

#### Scenario: Load into the calculator
- **WHEN** the user chooses to edit "Friday night" in the calculator
- **THEN** the calculator shows the portions, portion weight, water and ingredients of
  "Friday night"

#### Scenario: Ingredients are replaced, not merged
- **WHEN** the calculator has Honey and the user loads "Friday night", which has Salt and Yeast
- **THEN** the calculator has Salt and Yeast and no Honey

### Requirement: Recipes survive app updates
Saved recipes SHALL NOT be lost or altered by an app update. After an update, every saved recipe
SHALL keep its name, portions, portion weight, ingredients and percentages, and SHALL produce the same
ingredient weights as before the update.

A recipe saved before ingredients could be named SHALL, after the update, have Salt, Yeast and Olive
oil as its ingredients in that order, each with its old percentage, leaving out any that were at 0%.
Salt and Olive oil SHALL have a step of 0.1, Yeast a step of 0.01 and water a step of 1, or a finer
step where the old value needs it, so no value changes.
The calculator's remembered values SHALL be updated the same way.

#### Scenario: Recipe after an update
- **WHEN** the app is updated to a new version
- **THEN** every recipe saved before the update is still in the list
- **AND** each shows the same ingredient weights as before

#### Scenario: Fixed ingredients become named ingredients
- **WHEN** a recipe saved before ingredients could be named has 3% salt, 0.2% yeast and 0% olive oil
- **THEN** after the update it has Salt 3.0% and Yeast 0.20%, in that order, and no Olive oil
- **AND** its weights are the same as before the update
