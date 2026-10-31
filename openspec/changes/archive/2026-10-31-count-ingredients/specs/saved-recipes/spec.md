# Spec Delta

## MODIFIED Requirements

### Requirement: What a saved recipe holds
A saved recipe SHALL hold a name, a number of portions, a portion weight, water's percentage, and
its own list of other ingredients with their names, order, kinds, percentages and precision, or
counts and grams each. A saved recipe's ingredients SHALL belong to that recipe only: changing the
saved ingredients or another recipe SHALL NOT change them.

#### Scenario: Recipe keeps its portions
- **WHEN** the user saves a recipe with 6 portions of 280 g and opens it later
- **THEN** it opens with 6 portions of 280 g

#### Scenario: Recipe keeps its ingredients
- **WHEN** the user saves a recipe with Salt 3.0% and Honey 2.50%, in that order, and loads it later
- **THEN** the calculator shows Salt 3.0% then Honey 2.50%, with steps of 0.1 and 0.01

#### Scenario: Recipe keeps its counts
- **WHEN** the user saves a recipe with Eggs at 2 with 50 g each for 4 portions and loads it later
- **THEN** the calculator shows Eggs at 2 with 50 g each

#### Scenario: Recipe saved before counts existed
- **WHEN** a recipe saved before this update has Salt 3%
- **THEN** Salt is still a percentage ingredient at 3% with the same weight
