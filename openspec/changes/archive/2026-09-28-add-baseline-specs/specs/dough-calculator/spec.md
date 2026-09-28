# Spec Delta

## Purpose

Lets the user work out a batch of dough by choosing how many portions to make, how heavy each
portion is and the ingredient percentages, and save the result as a recipe.

## ADDED Requirements

### Requirement: Calculator screen
The app SHALL open on the dough calculator. It SHALL show the portions, the portion weight, a
control for every adjustable ingredient's percentage, the weights table for the current values, and
a "Start kneading" button.

#### Scenario: App opens on the calculator
- **WHEN** the user opens the app
- **THEN** the dough calculator is shown with the weights table for its current values

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
The user SHALL set the portion weight between 100 g and 500 g in steps of 10 g, with a slider and
with − and + buttons that move one step. The screen SHALL call it "Portion weight" and show the
current value in grams.

#### Scenario: Fine-tune the portion weight
- **WHEN** the portion weight is 250 g and the user taps +
- **THEN** the portion weight becomes 260 g

### Requirement: Ingredient percentage controls
For every adjustable ingredient, the calculator SHALL show its name, its current percentage, a
slider across its range, and − and + buttons that move one step. Water's control SHALL be labelled
"Hydration"; every other ingredient's control SHALL use the ingredient's name. The slider SHALL snap
to the ingredient's step. The − button SHALL be disabled at the bottom of the range and the + button
at the top.

#### Scenario: Water is labelled as hydration
- **WHEN** the calculator shows water at 62%
- **THEN** its control reads "Hydration" and "62%"

#### Scenario: Buttons move one step
- **WHEN** salt is 3% and the user taps + next to salt
- **THEN** salt becomes 3.1%

#### Scenario: Buttons stop at the range ends
- **WHEN** olive oil is 0%
- **THEN** the − button next to olive oil is disabled

### Requirement: Default values
On first use the calculator SHALL show 4 portions of 250 g with the default ingredient percentages.

#### Scenario: First launch
- **WHEN** the app is opened for the first time
- **THEN** the calculator shows 4 portions of 250 g and the default percentages

### Requirement: Remembered values
The calculator SHALL remember its portions, portion weight and percentages when the app is closed,
and show them again the next time it opens.

#### Scenario: Values survive a restart
- **WHEN** the user sets 6 portions and 68% water, then closes and reopens the app
- **THEN** the calculator shows 6 portions and 68% water

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
new recipe under a new name. When the loaded recipe has since been deleted, saving SHALL behave as
saving a new recipe.

#### Scenario: Update the loaded recipe
- **WHEN** the user loads "Friday night" into the calculator, changes water to 65% and chooses to
  update it
- **THEN** "Friday night" now has 65% water
- **AND** no new recipe is added

#### Scenario: Save the loaded recipe as new
- **WHEN** the user loads "Friday night", changes water to 65% and chooses to save as new with the
  name "Wetter Friday"
- **THEN** "Wetter Friday" is added with 65% water
- **AND** "Friday night" is unchanged

#### Scenario: Loaded recipe was deleted
- **WHEN** the recipe the calculator was loaded from has been deleted and the user saves
- **THEN** only saving as a new recipe is offered

### Requirement: Start kneading
The "Start kneading" button SHALL open the full recipe for the calculator's current values, titled
"Full recipe".

#### Scenario: Start kneading from the calculator
- **WHEN** the calculator shows 6 portions of 280 g and the user taps "Start kneading"
- **THEN** the full recipe opens titled "Full recipe" with 6 portions of 280 g and the calculator's
  percentages
