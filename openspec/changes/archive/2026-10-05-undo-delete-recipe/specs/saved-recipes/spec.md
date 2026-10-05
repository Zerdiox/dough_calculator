# Spec Delta

## MODIFIED Requirements

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
