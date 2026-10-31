# Spec Delta

## ADDED Requirements

### Requirement: Count ingredients
An ingredient other than flour and water SHALL be either a percentage ingredient or a count
ingredient. A count ingredient SHALL have an amount from 0 to 100 in steps of 0.5, for the number of
portions it was set at, and optionally a weight in grams for each one. When the portions change, the
amount SHALL scale in proportion and be rounded half up to the nearest 0.5. An amount above 0 SHALL
never scale below 0.5. Going back to the portions it was set at SHALL give the amount that was set.

#### Scenario: Count follows the portions
- **WHEN** Eggs is set to 2 at 4 portions
- **THEN** Eggs is 0.5 at 1 portion, 1 at 2 portions, 1.5 at 3 portions and 3 at 6 portions

#### Scenario: Never below a half
- **WHEN** Eggs is set to 1 at 10 portions and the portions become 1
- **THEN** Eggs is 0.5

#### Scenario: Back to the original portions
- **WHEN** Eggs is set to 2 at 4 portions, and the portions go to 3 and back to 4
- **THEN** Eggs is 2

## MODIFIED Requirements

### Requirement: Ingredient list
A recipe SHALL be made of flour, water and any number of other ingredients. Flour and water SHALL be
expressed as a percentage of the flour weight, and every other ingredient as a percentage of the
flour weight or as a count. Flour SHALL always be 100% and SHALL NOT be adjustable. Water SHALL
always be part of the recipe, with a percentage from 0% to 200%. Every other ingredient SHALL have a
name, a percentage from 0% to 200% or a count, and SHALL keep the place in the recipe the user gave
it.

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

### Requirement: Weights from baker's percentages
The weight of a count ingredient with grams each SHALL be its current amount multiplied by its grams
each. The flour weight SHALL be the total dough weight, less the weight of those count ingredients,
divided by one plus the sum of the percentages of water and every percentage ingredient (as
fractions). The weight of water and of every percentage ingredient SHALL be the flour weight
multiplied by its percentage. All these weights SHALL add up to the total dough weight. A count
ingredient without grams each SHALL have no weight and SHALL NOT affect any other weight.

When count ingredients weigh as much as the total dough weight or more, flour, water and the
percentage ingredients SHALL be 0 g, and the weights table SHALL say "Counted ingredients use up the
whole dough weight".

#### Scenario: Weights for a 1000 g dough
- **WHEN** a recipe totals 1000 g with water 65%, Salt 3% and Yeast 0.2%
- **THEN** flour is about 594.5 g (1000 / 1.682)
- **AND** water is 65%, Salt 3% and Yeast 0.2% of that flour weight
- **AND** all weights together add up to 1000 g

#### Scenario: More portions scale every ingredient
- **WHEN** the number of portions is doubled
- **THEN** every ingredient weight doubles

#### Scenario: Count with grams each
- **WHEN** a recipe totals 1000 g with water 60% and Eggs at 2 with 50 g each
- **THEN** Eggs weighs 100 g and flour is 562.5 g (900 / 1.6)
- **AND** all weights together add up to 1000 g

#### Scenario: Count without grams each
- **WHEN** a recipe totals 1000 g with water 60% and Eggs at 2 with no grams each
- **THEN** flour is 625 g (1000 / 1.6), the same as without Eggs

#### Scenario: Count ingredients too heavy
- **WHEN** a recipe totals 100 g and Eggs is at 4 with 50 g each
- **THEN** flour, water and the percentage ingredients are 0 g
- **AND** the weights table says "Counted ingredients use up the whole dough weight"

### Requirement: Weights table
Wherever the app shows a recipe's weights, it SHALL show a total bar followed by one row per
ingredient: Flour, then Water, then the recipe's other ingredients in the recipe's order. The total
bar SHALL read "TOTAL DOUGH", show the portions and portion weight as "<portions> × <weight> g", and
show the total dough weight in grams. Each percentage row SHALL show the ingredient's name, its
percentage and its weight. Each count row SHALL show the ingredient's name and its amount as
"× <amount>" with no trailing zeros, followed by its weight only when it has grams each. Percentage
ingredients whose weight is 0 g and count ingredients at 0 SHALL be left out of the table. The
weights SHALL update as soon as any input changes.

#### Scenario: Table for the default recipe
- **WHEN** the table shows 4 portions of 250 g with the default recipe
- **THEN** the total bar reads "TOTAL DOUGH", "4 × 250 g" and "1000 g"
- **AND** only rows for Flour and Water follow, in that order

#### Scenario: Other ingredients follow water in recipe order
- **WHEN** a recipe has Salt 3% and Yeast 0.2%, in that order
- **THEN** the rows read Flour, Water, Salt, Yeast

#### Scenario: Unused ingredient is hidden
- **WHEN** an ingredient is at 0%
- **THEN** the table has no row for it

#### Scenario: Count row with a weight
- **WHEN** Eggs is at 1.5 with 50 g each
- **THEN** its row shows "Eggs", "× 1.5" and "75 g", and no percentage

#### Scenario: Count row without a weight
- **WHEN** Eggs is at 2 with no grams each
- **THEN** its row shows "Eggs" and "× 2", with no percentage and no weight

#### Scenario: Unused count is hidden
- **WHEN** a count ingredient is at 0
- **THEN** the table has no row for it
