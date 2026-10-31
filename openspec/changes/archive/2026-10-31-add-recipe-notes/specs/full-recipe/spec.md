## MODIFIED Requirements

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
