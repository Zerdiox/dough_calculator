# Spec Delta

## MODIFIED Requirements

### Requirement: Add names when saving a recipe
A name SHALL join the saved ingredients only when the user chooses it while saving a recipe. When
the recipe being saved has ingredients whose names are not saved ingredients, ignoring upper and
lower case, the save dialogs SHALL list those names under "Add to saved ingredients", each with a
checkbox that starts unticked. This list SHALL be shown in the dialog that asks for a new recipe's
name, whether saving from the calculator or from the Convert screen, and in the dialog that offers
to update the loaded recipe or save it as new; ticks made in the latter SHALL carry over when the
user chooses to save as new. When saving completes, the ticked names SHALL be added. Cancelling
SHALL add nothing. When every ingredient's name is already saved, the list SHALL NOT be shown.

#### Scenario: Tick a new ingredient
- **WHEN** the recipe has Salt and Honey, Salt is saved, and the user ticks Honey and saves
- **THEN** Honey is added to the saved ingredients

#### Scenario: Leave it unticked
- **WHEN** the recipe has Truffle oil and the user saves without ticking it
- **THEN** the recipe is saved with Truffle oil
- **AND** Truffle oil is not added to the saved ingredients

#### Scenario: Update the loaded recipe
- **WHEN** the calculator was loaded from "Friday night", the user adds Honey, saves, ticks Honey
  and chooses to update "Friday night"
- **THEN** "Friday night" has Honey and Honey is added to the saved ingredients

#### Scenario: Save from Convert
- **WHEN** a converted recipe has Malt, Malt is not saved, and the user ticks Malt and saves it as a
  recipe
- **THEN** Malt is added to the saved ingredients

#### Scenario: Nothing new
- **WHEN** every ingredient in the recipe is a saved ingredient
- **THEN** the save dialogs show no "Add to saved ingredients" list

#### Scenario: Cancel adds nothing
- **WHEN** the user ticks Honey and then cancels the save
- **THEN** Honey is not added to the saved ingredients
