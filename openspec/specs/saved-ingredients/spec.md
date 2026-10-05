# saved-ingredients Specification

## Purpose
Keeps a short list of ingredient names the user chose to reuse, so adding a common ingredient to a
recipe is a pick from a list instead of typing, without one-off ingredients cluttering it.

## Requirements

### Requirement: What the saved ingredients hold
The saved ingredients SHALL be a list of ingredient names shared by all recipes. They SHALL hold
names only, no percentages. Each name SHALL appear once, ignoring upper and lower case. Wherever
they are shown, they SHALL be listed alphabetically. Adding or deleting a saved ingredient SHALL NOT
change any recipe or the calculator.

#### Scenario: Names only
- **WHEN** Yeast is added to the saved ingredients from a recipe with 0.2% yeast
- **THEN** picking Yeast in another recipe adds it at 0%

#### Scenario: Alphabetical
- **WHEN** the saved ingredients are Yeast, Honey and Salt, added in that order
- **THEN** they are listed as Honey, Salt, Yeast

### Requirement: Starting saved ingredients
On first use, the saved ingredients SHALL be Olive oil, Salt and Yeast. Someone updating from a
version without saved ingredients SHALL get the same list.

#### Scenario: First launch
- **WHEN** the app is opened for the first time
- **THEN** the saved ingredients are Olive oil, Salt and Yeast

### Requirement: Add names when saving a recipe
A name SHALL join the saved ingredients only when the user chooses it while saving a recipe. When
the recipe being saved has ingredients whose names are not saved ingredients, ignoring upper and
lower case, the save dialogs SHALL list those names under "Add to saved ingredients", each with a
checkbox that starts unticked. This list SHALL be shown in the dialog that asks for a new recipe's
name and in the dialog that offers to update the loaded recipe or save it as new; ticks made in the
latter SHALL carry over when the user chooses to save as new. When saving completes, the ticked
names SHALL be added. Cancelling SHALL add nothing. When every ingredient's name is already saved,
the list SHALL NOT be shown.

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

#### Scenario: Nothing new
- **WHEN** every ingredient in the recipe is a saved ingredient
- **THEN** the save dialogs show no "Add to saved ingredients" list

#### Scenario: Cancel adds nothing
- **WHEN** the user ticks Honey and then cancels the save
- **THEN** Honey is not added to the saved ingredients

### Requirement: Saved ingredients screen
The saved recipes SHALL offer a way to open the saved ingredients, on a screen titled "Saved
ingredients" that lists them. Back SHALL return to the saved recipes. When there are no saved
ingredients, the screen SHALL say so and explain that ingredients are added by ticking them when
saving a recipe.

#### Scenario: Open the saved ingredients
- **WHEN** the user opens the saved recipes and then the saved ingredients
- **THEN** the screen "Saved ingredients" lists the saved ingredients alphabetically

#### Scenario: No saved ingredients
- **WHEN** every saved ingredient has been deleted
- **THEN** the screen says there are no saved ingredients and how to add one

### Requirement: Delete a saved ingredient
Each saved ingredient SHALL offer to delete it, without asking for confirmation. After deleting,
the screen SHALL show a message "Deleted "<name>"" with an Undo action. Undo SHALL bring the name
back. Undo SHALL stay offered for about ten seconds while the screen is open, and SHALL end when the
user leaves the screen. Deleting another saved ingredient while the message shows SHALL replace the
message; the earlier one SHALL stay deleted.

#### Scenario: Delete and undo
- **WHEN** the user deletes Salt and taps Undo
- **THEN** Salt is back in the saved ingredients

#### Scenario: Recipes keep the ingredient
- **WHEN** "Friday night" has Salt and the user deletes Salt from the saved ingredients
- **THEN** "Friday night" still has Salt with its percentage

#### Scenario: Deleted ingredient is no longer offered
- **WHEN** the user deletes Yeast and then adds an ingredient to a recipe
- **THEN** the dropdown does not offer Yeast
