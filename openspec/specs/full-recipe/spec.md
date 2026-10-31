# full-recipe Specification

## Purpose

Gives the user a view of a recipe to follow while making dough, where the batch size can be adjusted
for this one bake and the screen does not turn off mid-way.

## Requirements

### Requirement: Full recipe screen
The full recipe SHALL show its title, the portions and portion weight controls, the weights table
for the recipe, and the recipe's note under the weights table with its bullet and numbered lists
formatted. With no note, nothing SHALL be shown in its place. The note SHALL NOT be editable here. It
SHALL NOT offer controls for the ingredient percentages. Back SHALL return to the screen it was opened
from, except when it was opened by saving a recipe from the Convert screen: then Back SHALL return to
the calculator.

#### Scenario: What the full recipe shows
- **WHEN** the user opens the full recipe for "Friday night"
- **THEN** the screen shows "Friday night", the portions, the portion weight and the weights table
- **AND** no ingredient percentage controls are shown

#### Scenario: Opened by saving from Convert
- **WHEN** the user saves a converted recipe with "Add more" unticked and then goes back from its
  full recipe
- **THEN** the calculator is shown

#### Scenario: Note under the weights table
- **WHEN** "Friday night" has a note with numbered steps and the user opens its full recipe
- **THEN** the note is shown under the weights table, with the steps numbered
- **AND** the note can't be changed on this screen

#### Scenario: No note
- **WHEN** a recipe has no note and the user opens its full recipe
- **THEN** nothing is shown for a note

#### Scenario: Note when kneading from the calculator
- **WHEN** the calculator has a note and the user taps "Start kneading"
- **THEN** the full recipe shows that note under the weights table

#### Scenario: Note stays when adjusting for this bake
- **WHEN** the user changes the portions on a full recipe with a note
- **THEN** the note is still shown and does not change

### Requirement: Temporary portions and portion weight
The full recipe SHALL open with the recipe's portions and portion weight. The user SHALL be able to
change both, within the same limits and steps as on the calculator, and the weights table, count
ingredients' amounts included, SHALL follow. These changes SHALL apply only while the full recipe is
open: they SHALL NOT change the saved recipe or the calculator, and the next time the recipe is
opened it SHALL start from the recipe's own values again.

#### Scenario: Adjust for this bake
- **WHEN** "Friday night" has 4 portions and the user sets 6 portions on its full recipe
- **THEN** the weights table shows the weights for 6 portions

#### Scenario: Count follows for this bake
- **WHEN** "Friday night" has Eggs at 2 for 4 portions and the user sets 3 portions on its full recipe
- **THEN** the weights table shows Eggs "× 1.5"
- **AND** opening it again shows Eggs "× 2" at 4 portions

#### Scenario: Saved recipe is untouched
- **WHEN** the user sets 6 portions on the full recipe of "Friday night" and goes back
- **THEN** "Friday night" still has 4 portions
- **AND** opening it again starts at 4 portions

#### Scenario: Calculator is untouched
- **WHEN** the user taps "Start kneading", changes the portion weight on the full recipe and goes
  back
- **THEN** the calculator still shows its own portion weight

### Requirement: Screen stays on
While the full recipe is open, the device screen SHALL stay on, and the screen SHALL say so.

#### Scenario: Screen stays on while kneading
- **WHEN** the full recipe is open and the user does not touch the device
- **THEN** the screen does not turn off
- **AND** a note says the screen stays on while this recipe is open
