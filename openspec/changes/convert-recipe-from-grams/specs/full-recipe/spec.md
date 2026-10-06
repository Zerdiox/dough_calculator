# Spec Delta

## MODIFIED Requirements

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
