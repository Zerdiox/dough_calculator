## ADDED Requirements

### Requirement: Recipe notes on the calculator
The calculator SHALL show an optional multi-line field labelled "Notes" at the very bottom of the
screen, below "Start kneading". Typing `-` followed by a space at the start of a line SHALL start a
bullet list, and typing a number and a period followed by a space, such as `1. `, SHALL start a
numbered list. The field SHALL show the lists formatted while typing. Enter at the end of a list item
SHALL start the next item, and Enter on an empty item SHALL end the list. The screen SHALL NOT offer
a formatting toolbar.

The note SHALL be one of the calculator's values: the calculator SHALL remember it when the app is
closed, and it SHALL be saved when the user saves a new recipe or updates a loaded recipe. Changing
the note SHALL NOT change a saved recipe until the recipe is saved, and SHALL NOT change the weights.
The Notes field SHALL stay available while the ingredients are being edited, and Done SHALL keep the
note as it then stands. On first use the note SHALL be empty.

#### Scenario: Notes field position
- **WHEN** the calculator is open
- **THEN** the Notes field is shown below "Start kneading", as the last thing on the screen

#### Scenario: Typing a bullet list
- **WHEN** the user types "- 100 g whole wheat" in Notes
- **THEN** the line is shown as a bullet item reading "100 g whole wheat"

#### Scenario: Typing numbered steps
- **WHEN** the user types "1. Mix", presses Enter and types "Rest"
- **THEN** the lines are shown as numbered steps 1 and 2

#### Scenario: Ending a list
- **WHEN** the user presses Enter on an empty list item
- **THEN** the list ends and the next text is normal text

#### Scenario: Note remembered
- **WHEN** the user writes a note, closes the app and opens it again
- **THEN** the Notes field shows the note with its lists

#### Scenario: Note reads back as typed
- **WHEN** the user writes "Flour", then a bullet list, and "2 * 3 * 4" on another line, and opens
  the app again
- **THEN** the note shows the same lines and symbols, with no blank line added

#### Scenario: Clearing the note
- **WHEN** the user clears all the text in Notes
- **THEN** the recipe has no note

#### Scenario: Note kept when the phone is rotated
- **WHEN** the user writes a note and rotates the phone
- **THEN** the Notes field still shows the note with its lists

#### Scenario: Note saved with a new recipe
- **WHEN** the user writes a note and saves the calculator as "Friday night"
- **THEN** the full recipe of "Friday night" shows the note

#### Scenario: Note updated on a loaded recipe
- **WHEN** the user loads "Friday night", changes the note and chooses to update it
- **THEN** the full recipe of "Friday night" shows the new note

#### Scenario: Note not saved until the recipe is saved
- **WHEN** the user loads "Friday night" and changes the note without saving
- **THEN** the full recipe of "Friday night" opened from the saved recipes shows the old note

#### Scenario: Weights ignore the note
- **WHEN** the user changes the note
- **THEN** the weights table does not change

#### Scenario: Change the note while editing ingredients
- **WHEN** the user taps Edit on the Ingredients card, adds an ingredient, changes the note and taps
  Done
- **THEN** the calculator has both the new ingredient and the changed note
