# Spec Delta

## MODIFIED Requirements

### Requirement: Converted recipe preview
While the flour weight can be used, the Convert screen SHALL show the weights table for the recipe
made from the input that can be used so far, and update it as soon as any input changes. A row
whose name or grams can't be used yet, because it is blank or its field shows a message, SHALL be
left out of the table while the other rows stay. Water that can't be used SHALL count as 0 g in the
table. The portion weight in the table SHALL come from the weights shown in it, even when the
screen says "Portions under 5 g". While the flour weight is blank, 0 or not a weight, the place of
the table SHALL say "Enter the flour weight to see the recipe".

While any field shows a message, the screen says "Portions under 5 g", a name is blank, or Flour is
blank, both "Save as recipe" and "Use in calculator" SHALL be disabled.

#### Scenario: Live preview
- **WHEN** the user types Flour 500 and Water 325 with 4 portions
- **THEN** the weights table reads "4 × 206 g" with Flour and Water rows at 100% and 65%

#### Scenario: Adding an ingredient keeps the table
- **WHEN** the user has typed Flour 500 and Water 325 with 4 portions and taps "Add ingredient"
- **THEN** the weights table still reads "4 × 206 g" with Flour and Water rows
- **AND** both buttons are disabled

#### Scenario: Unfinished input
- **WHEN** the user has typed Flour 500 and Water 325 with 4 portions, and a row has 15 g but a
  blank name
- **THEN** the weights table reads "4 × 206 g" with Flour and Water rows and no row for the 15 g
- **AND** both buttons are disabled

#### Scenario: A row with a message is left out
- **WHEN** the user types Flour 100, Water 60 and Salt 250 with 4 portions
- **THEN** the Salt field says "Over 200% of the flour"
- **AND** the weights table reads "4 × 40 g" with Flour and Water rows and no Salt row
- **AND** both buttons are disabled

#### Scenario: Water with a message
- **WHEN** the user types Flour 500 and "abc" for Water
- **THEN** the weights table shows Water at 0%
- **AND** both buttons are disabled

#### Scenario: Small portions still show
- **WHEN** the user types Flour 15 and Water 3 with 4 portions
- **THEN** the screen says "Portions under 5 g"
- **AND** the weights table reads "4 × 4 g"
- **AND** both buttons are disabled

#### Scenario: No flour yet
- **WHEN** the Flour field is blank
- **THEN** no weights table is shown and its place says "Enter the flour weight to see the recipe"
- **AND** both buttons are disabled
