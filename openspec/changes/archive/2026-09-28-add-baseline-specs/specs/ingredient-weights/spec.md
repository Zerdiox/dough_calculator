# Spec Delta

## Purpose

Defines the ingredients a dough is made of, how their weights follow from baker's percentages, and
how those weights are shown wherever the app shows a recipe.

## ADDED Requirements

### Requirement: Ingredient list
The app SHALL work with these ingredients, in this order, each expressed as a percentage of the
flour weight:

| Ingredient | Percentage range | Step | Default |
|------------|------------------|------|---------|
| Flour      | always 100%      | -    | 100%    |
| Water      | 50% to 90%       | 1    | 62%     |
| Salt       | 0% to 5%         | 0.1  | 3%      |
| Yeast      | 0% to 3%         | 0.05 | 0.2%    |
| Olive oil  | 0% to 6%         | 0.5  | 0%      |

Flour SHALL NOT be adjustable. Every other ingredient's percentage SHALL stay within its range and
land on a multiple of its step.

#### Scenario: Fresh recipe uses the default percentages
- **WHEN** a recipe is created without any saved values
- **THEN** water is 62%, salt 3%, yeast 0.2% and olive oil 0%

#### Scenario: Percentage stays on a step within its range
- **WHEN** the user sets salt to a value between two steps or beyond 5%
- **THEN** salt becomes the nearest multiple of 0.1 within 0% to 5%

### Requirement: Total dough weight
The total dough weight SHALL be the number of portions multiplied by the portion weight.

#### Scenario: Four portions of 250 g
- **WHEN** a recipe has 4 portions of 250 g
- **THEN** the total dough weight is 1000 g

### Requirement: Weights from baker's percentages
The flour weight SHALL be the total dough weight divided by one plus the sum of the other
ingredients' percentages (as fractions). Every other ingredient's weight SHALL be the flour weight
multiplied by its percentage. The ingredient weights SHALL add up to the total dough weight.

#### Scenario: Weights for a 1000 g dough
- **WHEN** a recipe totals 1000 g with water 65%, salt 3%, yeast 0.2% and olive oil 0%
- **THEN** flour is about 594.5 g (1000 / 1.682)
- **AND** water is 65%, salt 3% and yeast 0.2% of that flour weight
- **AND** all weights together add up to 1000 g

#### Scenario: More portions scale every ingredient
- **WHEN** the number of portions is doubled
- **THEN** every ingredient weight doubles

### Requirement: Weights table
Wherever the app shows a recipe's weights, it SHALL show a total bar followed by one row per
ingredient. The total bar SHALL read "TOTAL DOUGH", show the portions and portion weight as
"<portions> × <weight> g", and show the total dough weight in grams. Each ingredient row SHALL show
the ingredient's name, its percentage and its weight. Ingredients whose weight is 0 g SHALL be left
out of the table. The weights SHALL update as soon as any input changes.

#### Scenario: Table for the default recipe
- **WHEN** the table shows 4 portions of 250 g with the default percentages
- **THEN** the total bar reads "TOTAL DOUGH", "4 × 250 g" and "1000 g"
- **AND** rows for Flour, Water, Salt and Yeast follow, in that order

#### Scenario: Unused ingredient is hidden
- **WHEN** olive oil is at 0%
- **THEN** the table has no Olive oil row

### Requirement: Rounding in the weights table
Weights under 10 g SHALL be shown with at most one decimal; weights of 10 g or more SHALL be shown in
whole grams. Percentages SHALL be shown with at most two decimals. Trailing zeros SHALL be dropped,
and rounding SHALL be half up.

#### Scenario: Small weight keeps one decimal
- **WHEN** yeast weighs 1.19 g
- **THEN** the table shows "1.2 g"

#### Scenario: Large weight in whole grams
- **WHEN** flour weighs 594.5 g
- **THEN** the table shows "595 g"

#### Scenario: Percentage without trailing zeros
- **WHEN** water is 62% and yeast is 0.25%
- **THEN** the table shows "62%" and "0.25%"
