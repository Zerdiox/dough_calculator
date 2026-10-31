# Spec Delta

## MODIFIED Requirements

### Requirement: Temporary portions and portion weight
The full recipe SHALL open with the recipe's portions and portion weight. The user SHALL be able to
change both, within the same limits and steps as on the calculator, and the weights table, count
ingredients' amounts included, SHALL follow. These changes SHALL apply only while the full recipe is
open: they SHALL NOT change the saved recipe or the calculator, and the next time the recipe is
opened it SHALL start from the recipe's own values again.

#### Scenario: Adjust for this bake
- **WHEN** "Friday night" has 4 portions and the user sets 6 portions on its full recipe
- **THEN** the weights table shows the weights for 6 portions

#### Scenario: Count follows for this bake
- **WHEN** "Friday night" has Eggs at 2 for 4 portions and the user sets 3 portions on its full recipe
- **THEN** the weights table shows Eggs "× 1.5"
- **AND** opening it again shows Eggs "× 2" at 4 portions

#### Scenario: Saved recipe is untouched
- **WHEN** the user sets 6 portions on the full recipe of "Friday night" and goes back
- **THEN** "Friday night" still has 4 portions
- **AND** opening it again starts at 4 portions

#### Scenario: Calculator is untouched
- **WHEN** the user taps "Start kneading", changes the portion weight on the full recipe and goes
  back
- **THEN** the calculator still shows its own portion weight
