# Spec Delta

## Purpose

Lets the user keep dough recipes under a name, find them again, follow or change them, and remove the
ones they no longer want, without ever losing a recipe to an app update.

## ADDED Requirements

### Requirement: What a saved recipe holds
A saved recipe SHALL hold a name, a number of portions, a portion weight and a percentage for every
adjustable ingredient.

#### Scenario: Recipe keeps its portions
- **WHEN** the user saves a recipe with 6 portions of 280 g and opens it later
- **THEN** it opens with 6 portions of 280 g

### Requirement: Saved recipes list
The user SHALL reach the saved recipes from the calculator. The list SHALL show the recipes in the
order they were saved, oldest first. Each entry SHALL show the recipe's name and a summary reading
"<portions> × <weight> g · <water>% water", with the water percentage shown with at most one decimal.
Back SHALL return to the calculator.

#### Scenario: Entry summary
- **WHEN** "Friday night" has 4 portions of 250 g and 62% water
- **THEN** its entry shows "Friday night" and "4 × 250 g · 62% water"

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
calculator's values (portions, portion weight and percentages) with the recipe's, without asking,
and return to the calculator, which then remembers which recipe it was loaded from.

#### Scenario: Load into the calculator
- **WHEN** the user chooses to edit "Friday night" in the calculator
- **THEN** the calculator shows the portions, portion weight and percentages of "Friday night"

### Requirement: Delete a saved recipe
Each saved recipe SHALL offer to delete it. Deleting SHALL first ask for confirmation, naming the
recipe, with Delete and Cancel. After deleting, the app SHALL show a message naming the deleted
recipe with an Undo action. Undo SHALL bring the recipe back unchanged, in its original place in the
list.

#### Scenario: Cancel the delete
- **WHEN** the user taps delete on "Friday night" and then Cancel
- **THEN** "Friday night" stays in the list

#### Scenario: Confirm the delete
- **WHEN** the user taps delete on "Friday night" and then Delete
- **THEN** "Friday night" is removed from the list
- **AND** a message about the deleted "Friday night" offers Undo

#### Scenario: Undo the delete
- **WHEN** the user taps Undo after deleting "Friday night"
- **THEN** "Friday night" is back in the list, in the same place, with the same values

### Requirement: Recipes survive app updates
Saved recipes SHALL NOT be lost or altered by an app update. After an update, every saved recipe
SHALL keep its name, portions, portion weight, ingredients and percentages, and SHALL produce the same
ingredient weights as before the update.

#### Scenario: Recipe after an update
- **WHEN** the app is updated to a new version
- **THEN** every recipe saved before the update is still in the list
- **AND** each shows the same ingredient weights as before
