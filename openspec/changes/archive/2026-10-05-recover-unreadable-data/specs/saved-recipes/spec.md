# Spec Delta

## ADDED Requirements

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
