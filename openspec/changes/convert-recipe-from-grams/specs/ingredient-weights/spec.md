# Spec Delta

## MODIFIED Requirements

### Requirement: Ingredient list
A recipe SHALL be made of flour, water and any number of other ingredients, each expressed as a
percentage of the flour weight. Flour SHALL always be 100% and SHALL NOT be adjustable. Water SHALL
always be part of the recipe, with a percentage from 0% to 200%. Every other ingredient SHALL have a
name and a percentage from 0% to 200%, and SHALL keep the place in the recipe the user gave it.

A new recipe SHALL have water at 62% and no other ingredients.

#### Scenario: Fresh recipe uses the default percentages
- **WHEN** a recipe is created without any saved values
- **THEN** it has flour at 100% and water at 62%
- **AND** no other ingredients

#### Scenario: Other ingredients keep their order
- **WHEN** a recipe has Salt, Yeast and Honey, in that order
- **THEN** wherever the recipe is shown, they appear as Salt, Yeast, Honey

#### Scenario: Very wet dough
- **WHEN** the user sets water to 110%
- **THEN** the recipe has water at 110% and the weights follow

#### Scenario: Percentage stays on a step within its range
- **WHEN** the user tries to set an ingredient or water above 200%
- **THEN** the percentage is not accepted and the previous value stays
