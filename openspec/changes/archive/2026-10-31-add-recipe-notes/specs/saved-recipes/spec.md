## MODIFIED Requirements

### Requirement: What a saved recipe holds
A saved recipe SHALL hold a name, a number of portions, a portion weight, water's percentage, its own
list of other ingredients with their names, order, kinds, percentages and precision, or counts and
grams each, and its note. A saved recipe's ingredients SHALL belong to that recipe only: changing the
saved ingredients or another recipe SHALL NOT change them.

#### Scenario: Recipe keeps its portions
- **WHEN** the user saves a recipe with 6 portions of 280 g and opens it later
- **THEN** it opens with 6 portions of 280 g

#### Scenario: Recipe keeps its ingredients
- **WHEN** the user saves a recipe with Salt 3.0% and Honey 2.50%, in that order, and loads it later
- **THEN** the calculator shows Salt 3.0% then Honey 2.50%, with steps of 0.1 and 0.01

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

### Requirement: Load a saved recipe into the calculator
Each saved recipe SHALL offer to edit it in the calculator. Doing so SHALL replace all of the
calculator's values (portions, portion weight, water's percentage, the other ingredients and the note)
with the recipe's, without asking, and return to the calculator, which then remembers which recipe it
was loaded from.

#### Scenario: Load into the calculator
- **WHEN** the user chooses to edit "Friday night" in the calculator
- **THEN** the calculator shows the portions, portion weight, water, ingredients and note of
  "Friday night"

#### Scenario: Ingredients are replaced, not merged
- **WHEN** the calculator has Honey and the user loads "Friday night", which has Salt and Yeast
- **THEN** the calculator has Salt and Yeast and no Honey

#### Scenario: Note is replaced
- **WHEN** the calculator has a note and the user loads a recipe with no note
- **THEN** the Notes field is empty
