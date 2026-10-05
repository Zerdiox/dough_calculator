# Spec Delta

## MODIFIED Requirements

### Requirement: Update a loaded recipe
When the calculator's values were loaded from a saved recipe that still exists, saving SHALL offer
two choices: update that recipe with the calculator's current values, keeping its name, or save a
new recipe under a new name. Cancelling the choice SHALL save nothing. After updating, the app SHALL
confirm with a short message naming the recipe. The calculator SHALL keep remembering the recipe it
was loaded from after the app is closed and opened again. After saving as new, the calculator SHALL
count as loaded from the new recipe. When the loaded recipe has since been deleted, saving SHALL
behave as saving a new recipe.

#### Scenario: Update the loaded recipe
- **WHEN** the user loads "Friday night" into the calculator, changes water to 65% and chooses to
  update it
- **THEN** "Friday night" now has 65% water
- **AND** no new recipe is added
- **AND** a message confirms "Updated "Friday night""

#### Scenario: Save the loaded recipe as new
- **WHEN** the user loads "Friday night", changes water to 65% and chooses to save as new with the
  name "Wetter Friday"
- **THEN** "Wetter Friday" is added with 65% water
- **AND** "Friday night" is unchanged

#### Scenario: Loaded recipe was deleted
- **WHEN** the recipe the calculator was loaded from has been deleted and the user saves
- **THEN** only saving as a new recipe is offered

#### Scenario: Loaded recipe remembered after a restart
- **WHEN** the user loads "Friday night", closes the app, opens it again and saves
- **THEN** updating "Friday night" is offered

#### Scenario: Update after saving as new
- **WHEN** the user loads "Friday night", saves as new with the name "Wetter Friday", changes salt to
  2.5% and saves again
- **THEN** updating "Wetter Friday" is offered
