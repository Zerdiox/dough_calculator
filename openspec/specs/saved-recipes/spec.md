# saved-recipes Specification

## Purpose

Lets the user keep dough recipes under a name, find them again, follow or change them, and remove the
ones they no longer want, without ever losing a recipe to an app update.

## Requirements

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

### Requirement: Saved recipes list
The user SHALL reach the saved recipes from the calculator. The list SHALL show the recipes in the
order they were saved, oldest first. Each entry SHALL show the recipe's name and a summary reading
"<portions> × <weight> g · <water>% hydration", with the water percentage shown with at most one
decimal.
Back SHALL return to the calculator.

#### Scenario: Entry summary
- **WHEN** "Friday night" has 4 portions of 250 g and 62% water
- **THEN** its entry shows "Friday night" and "4 × 250 g · 62% hydration"

#### Scenario: Newest recipe last
- **WHEN** the user saves "A" and then "B"
- **THEN** the list shows "A" above "B"

### Requirement: No saved recipes yet
When there are no saved recipes, the list SHALL say so and explain that recipes are saved from the
calculator.

#### Scenario: Empty list
- **WHEN** the user opens the saved recipes before saving any
- **THEN** a message says there are no saved recipes yet and how to save one

### Requirement: Open a saved recipe
Tapping a saved recipe SHALL open its full recipe, titled with the recipe's name.

#### Scenario: Open a recipe
- **WHEN** the user taps "Friday night"
- **THEN** the full recipe opens titled "Friday night" with that recipe's values

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

### Requirement: Delete a saved recipe
Each saved recipe SHALL offer to delete it. Deleting SHALL first ask for confirmation, naming the
recipe, with Delete and Cancel. After deleting, the app SHALL show a message naming the deleted
recipe with an Undo action. Undo SHALL bring the recipe back unchanged, in its original place in the
list. Undo SHALL stay offered for about ten seconds while the saved recipes list is open, and SHALL
end when the user leaves the list. Deleting another recipe while the message shows SHALL replace the
message; the earlier recipe SHALL stay deleted.

#### Scenario: Cancel the delete
- **WHEN** the user taps delete on "Friday night" and then Cancel
- **THEN** "Friday night" stays in the list

#### Scenario: Confirm the delete
- **WHEN** the user taps delete on "Friday night" and then Delete
- **THEN** "Friday night" is removed from the list
- **AND** a message "Deleted "Friday night"" offers Undo

#### Scenario: Undo the delete
- **WHEN** the user taps Undo after deleting "Friday night"
- **THEN** "Friday night" is back in the list, in the same place, with the same values

#### Scenario: Undo a loaded recipe
- **WHEN** the calculator was loaded from "Friday night", the user deletes it, taps Undo and then
  saves the calculator
- **THEN** updating "Friday night" is offered

#### Scenario: Delete another recipe while the message shows
- **WHEN** the user deletes "Friday night" and, while its message shows, deletes "Party"
- **THEN** the message names "Party" and its Undo brings back "Party"
- **AND** "Friday night" stays deleted

#### Scenario: Undo ends when leaving the list
- **WHEN** the user deletes "Friday night" and goes back to the calculator before tapping Undo
- **THEN** "Friday night" stays deleted

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

### Requirement: Saved recipes that can't be read
If the app can't read its saved recipes, it SHALL keep a copy of the old data on the phone before
starting fresh with the default values, and SHALL NOT delete that copy by itself. The calculator
SHALL then show a message saying the saved recipes couldn't be read, the app started fresh, and a
copy of the old data was kept. The message SHALL have a single OK button. It SHALL keep appearing,
including after the app is closed and opened again, until the user taps OK, and SHALL NOT appear
again after that until the next time the saved recipes can't be read.

#### Scenario: Message after starting fresh
- **WHEN** the app opens and can't read the saved recipes
- **THEN** the calculator shows the default values and no saved recipes
- **AND** a message says the saved recipes couldn't be read and a copy of the old data was kept

#### Scenario: Message dismissed
- **WHEN** the user taps OK on the message and later reopens the app
- **THEN** the message does not appear again

#### Scenario: App closed before the message is seen
- **WHEN** the app closes while the message is showing, before the user taps OK
- **THEN** the message appears again the next time the app opens

#### Scenario: Recipes saved after starting fresh
- **WHEN** the user saves a new recipe after the app started fresh
- **THEN** the new recipe is kept, and the copy of the old data is still on the phone

#### Scenario: Normal start
- **WHEN** the app opens and reads the saved recipes without a problem
- **THEN** no message appears
