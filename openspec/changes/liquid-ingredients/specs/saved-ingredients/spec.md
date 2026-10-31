## ADDED Requirements

### Requirement: Change a saved ingredient
Each saved ingredient SHALL be changeable on the saved ingredients screen, with the same choice between
"%", "Liquid" and "Count" and the same "Grams each" field and rules as while editing a recipe's
ingredients. A change SHALL apply at once, with no Done button. While "Grams each" holds something that
can't be used, the field SHALL say "Enter grams" and the last usable value SHALL stay saved. Changing
to another kind and back SHALL restore the grams each, also after leaving the screen. A change SHALL
only affect what the ingredient starts as the next time it is picked, and SHALL NOT change any recipe
or the calculator. The saved ingredient's name SHALL NOT be changeable here.

#### Scenario: Make a saved ingredient a liquid
- **WHEN** Beer is saved as "%" and the user chooses "Liquid" for it
- **THEN** picking Beer while editing a recipe sets the row to "Liquid"

#### Scenario: Make a saved ingredient a count
- **WHEN** the user chooses "Count" for Eggs and types 50 for "Grams each"
- **THEN** picking Eggs while editing a recipe adds it as a count with 50 g each

#### Scenario: Grams each that can't be used
- **WHEN** Eggs has 50 g each and the user types "abc" for "Grams each"
- **THEN** the field says "Enter grams"
- **AND** Eggs is still saved with 50 g each

#### Scenario: Changing the kind back
- **WHEN** Eggs is a count with 50 g each, and the user chooses "%" and then "Count"
- **THEN** Eggs is a count with 50 g each

#### Scenario: Recipes keep their own kind
- **WHEN** "Beer dough" has Beer as a liquid and the user changes the saved Beer to "%"
- **THEN** "Beer dough" still has Beer as a liquid

## MODIFIED Requirements

### Requirement: What the saved ingredients hold
The saved ingredients SHALL be a list of ingredients shared by all recipes. Each SHALL hold a name,
its kind, and for a count its grams each, if any. They SHALL hold no percentages or amounts. Each name SHALL appear once,
ignoring upper and lower case. Wherever they are shown, they SHALL be listed alphabetically. Adding
or deleting a saved ingredient SHALL NOT change any recipe or the calculator. Saved ingredients from
before counts existed SHALL be percentage ingredients. Saved ingredients from before liquids could be
chosen SHALL stay percentage ingredients.

#### Scenario: Names only
- **WHEN** Yeast is added to the saved ingredients from a recipe with 0.2% yeast
- **THEN** picking Yeast in another recipe adds it at 0%

#### Scenario: Liquid keeps its kind
- **WHEN** Beer is added to the saved ingredients from a recipe with Beer set to Liquid
- **THEN** picking Beer while editing another recipe adds it at 0% set to "Liquid"

#### Scenario: Count keeps its setup but not its amount
- **WHEN** Eggs is added to the saved ingredients from a recipe with Eggs at 2 with 50 g each
- **THEN** picking Eggs while editing another recipe adds it as a count at 0 with 50 g each

#### Scenario: Alphabetical
- **WHEN** the saved ingredients are Yeast, Honey and Salt, added in that order
- **THEN** they are listed as Honey, Salt, Yeast

### Requirement: Starting saved ingredients
On first use, the saved ingredients SHALL be Olive oil, Salt, Water and Yeast, with only Water
liquid. Someone updating from a version without saved ingredients SHALL get the same list. Someone
updating from a version where water was a fixed part of every recipe SHALL keep their saved
ingredients and get Water added as a liquid.

#### Scenario: First launch
- **WHEN** the app is opened for the first time
- **THEN** the saved ingredients are Olive oil, Salt, Water and Yeast
- **AND** Water is liquid

#### Scenario: Water added by the update
- **WHEN** the saved ingredients are Honey and Salt before the update
- **THEN** after the update they are Honey, Salt and Water, with only Water liquid

### Requirement: Add names when saving a recipe
A name SHALL join the saved ingredients only when the user chooses it while saving a recipe. When
the recipe being saved has ingredients whose names are not saved ingredients, ignoring upper and
lower case, the save dialogs SHALL list those names under "Add to saved ingredients", each with a
checkbox that starts unticked. This list SHALL be shown in the dialog that asks for a new recipe's
name, whether saving from the calculator or from the Convert screen, and in the dialog that offers
to update the loaded recipe or save it as new; ticks made in the latter SHALL carry over when the
user chooses to save as new. When saving completes, the ticked names SHALL be added with the kind
and grams each they have in the recipe. Saving a recipe SHALL NOT change an ingredient
that is already saved. Cancelling SHALL add nothing. When every ingredient's name is already saved,
the list SHALL NOT be shown.

#### Scenario: Tick a new ingredient
- **WHEN** the recipe has Salt and Honey, Salt is saved, and the user ticks Honey and saves
- **THEN** Honey is added to the saved ingredients

#### Scenario: Tick a liquid
- **WHEN** the recipe has Beer set to Liquid, and the user ticks Beer in the list and saves
- **THEN** Beer is added to the saved ingredients as a liquid

#### Scenario: Tick a count
- **WHEN** the recipe has Eggs as a count with 50 g each, and the user ticks Eggs and saves
- **THEN** Eggs is added to the saved ingredients as a count with 50 g each

#### Scenario: Already saved stays as it is
- **WHEN** Eggs is saved as a count with 50 g each, and the user saves a recipe with Eggs at 60 g each
- **THEN** the saved ingredient Eggs still has 50 g each

#### Scenario: Saved kind stays as it is
- **WHEN** Beer is saved and not liquid, and the user saves a recipe with Beer set to Liquid
- **THEN** the saved ingredient Beer is still not liquid

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

### Requirement: Saved ingredients screen
The saved recipes SHALL offer a way to open the saved ingredients, on a screen titled "Saved
ingredients" that lists them. Each entry SHALL show its name, a delete button, the
choice of its kind and, for a count, its "Grams each" field.
Back SHALL return to the saved recipes. When there are no saved ingredients, the screen SHALL say so
and explain that ingredients are added by ticking them when saving a recipe.

#### Scenario: Open the saved ingredients
- **WHEN** the user opens the saved recipes and then the saved ingredients
- **THEN** the screen "Saved ingredients" lists the saved ingredients alphabetically

#### Scenario: Liquid in the list
- **WHEN** Water is saved as a liquid
- **THEN** its entry shows "Water" with "Liquid" selected

#### Scenario: Count in the list
- **WHEN** Eggs is saved as a count with 50 g each
- **THEN** its entry shows "Eggs" with "Count" selected and "Grams each" holding 50

#### Scenario: No saved ingredients
- **WHEN** every saved ingredient has been deleted
- **THEN** the screen says there are no saved ingredients and how to add one

### Requirement: Delete a saved ingredient
Each saved ingredient SHALL offer to delete it, without asking for confirmation. After deleting,
the screen SHALL show a message "Deleted "<name>"" with an Undo action. Undo SHALL bring it back with
its kind and grams each. Undo SHALL stay offered for about ten seconds while the screen
is open, and SHALL end when the user leaves the screen. Deleting another saved ingredient while the
message shows SHALL replace the message; the earlier one SHALL stay deleted.

#### Scenario: Delete and undo
- **WHEN** the user deletes Salt and taps Undo
- **THEN** Salt is back in the saved ingredients

#### Scenario: Undo keeps a liquid
- **WHEN** the user deletes Water, saved as a liquid, and taps Undo
- **THEN** Water is back as a liquid

#### Scenario: Undo keeps a count
- **WHEN** the user deletes Eggs, saved as a count with 50 g each, and taps Undo
- **THEN** Eggs is back as a count with 50 g each

#### Scenario: Recipes keep the ingredient
- **WHEN** "Friday night" has Salt and the user deletes Salt from the saved ingredients
- **THEN** "Friday night" still has Salt with its percentage

#### Scenario: Deleted ingredient is no longer offered
- **WHEN** the user deletes Yeast and then adds an ingredient to a recipe
- **THEN** the dropdown does not offer Yeast
