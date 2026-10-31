## MODIFIED Requirements

### Requirement: Use in calculator
"Use in calculator" SHALL replace all of the calculator's values with the converted recipe, without
asking, and return to the calculator. A converted recipe SHALL have no note, so the calculator's note
SHALL be cleared. The calculator SHALL then count as not loaded from any saved recipe, so saving it
SHALL offer only saving a new recipe.

#### Scenario: Send to the calculator
- **WHEN** the user converts Flour 500, Water 325, Salt 15 and Yeast 1 with 4 portions and taps
  "Use in calculator"
- **THEN** the calculator shows 4 portions of 210 g, Hydration at 65%, Salt at 3% and Yeast at 0.2%

#### Scenario: Not linked to the loaded recipe
- **WHEN** the calculator was loaded from "Friday night" and the user uses a converted recipe in it
  and then saves
- **THEN** only saving as a new recipe is offered

#### Scenario: Note is cleared
- **WHEN** the calculator has a note and the user uses a converted recipe in it
- **THEN** the Notes field is empty
