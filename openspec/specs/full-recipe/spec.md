# full-recipe Specification

## Purpose

Gives the user a view of a recipe to follow while making dough, where the batch size can be adjusted
for this one bake and the screen does not turn off mid-way.

## Requirements

### Requirement: Full recipe screen
The full recipe SHALL show its title, the portions and portion weight controls, and the weights
table for the recipe. It SHALL NOT offer controls for the ingredient percentages. Back SHALL return
to the screen it was opened from, except when it was opened by saving a recipe from the Convert
screen: then Back SHALL return to the calculator.

#### Scenario: What the full recipe shows
- **WHEN** the user opens the full recipe for "Friday night"
- **THEN** the screen shows "Friday night", the portions, the portion weight and the weights table
- **AND** no ingredient percentage controls are shown

#### Scenario: Opened by saving from Convert
- **WHEN** the user saves a converted recipe with "Add more" unticked and then goes back from its
  full recipe
- **THEN** the calculator is shown

### Requirement: Temporary portions and portion weight
The full recipe SHALL open with the recipe's portions and portion weight. The user SHALL be able to
change both, within the same limits and steps as on the calculator, and the weights table SHALL
follow. These changes SHALL apply only while the full recipe is open: they SHALL NOT change the saved
recipe or the calculator, and the next time the recipe is opened it SHALL start from the recipe's
own values again.

#### Scenario: Adjust for this bake
- **WHEN** "Friday night" has 4 portions and the user sets 6 portions on its full recipe
- **THEN** the weights table shows the weights for 6 portions

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
