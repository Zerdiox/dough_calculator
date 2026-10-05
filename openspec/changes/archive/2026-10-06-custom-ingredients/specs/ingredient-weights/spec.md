# Spec Delta

## MODIFIED Requirements

### Requirement: Ingredient list
A recipe SHALL be made of flour, water and any number of other ingredients, each expressed as a
percentage of the flour weight. Flour SHALL always be 100% and SHALL NOT be adjustable. Water SHALL
always be part of the recipe, with a percentage from 0% to 100%. Every other ingredient SHALL have a
name and a percentage from 0% to 100%, and SHALL keep the place in the recipe the user gave it.

A new recipe SHALL have water at 62% and no other ingredients.

#### Scenario: Fresh recipe uses the default percentages
- **WHEN** a recipe is created without any saved values
- **THEN** it has flour at 100% and water at 62%
- **AND** no other ingredients

#### Scenario: Other ingredients keep their order
- **WHEN** a recipe has Salt, Yeast and Honey, in that order
- **THEN** wherever the recipe is shown, they appear as Salt, Yeast, Honey

#### Scenario: Percentage stays on a step within its range
- **WHEN** the user tries to set an ingredient or water above 100%
- **THEN** the percentage is not accepted and the previous value stays

### Requirement: Weights from baker's percentages
The flour weight SHALL be the total dough weight divided by one plus the sum of the percentages of
water and every other ingredient (as fractions). Every other ingredient's weight, water included,
SHALL be the flour weight multiplied by its percentage. The ingredient weights SHALL add up to the
total dough weight.

#### Scenario: Weights for a 1000 g dough
- **WHEN** a recipe totals 1000 g with water 65%, Salt 3% and Yeast 0.2%
- **THEN** flour is about 594.5 g (1000 / 1.682)
- **AND** water is 65%, Salt 3% and Yeast 0.2% of that flour weight
- **AND** all weights together add up to 1000 g

#### Scenario: More portions scale every ingredient
- **WHEN** the number of portions is doubled
- **THEN** every ingredient weight doubles

### Requirement: Weights table
Wherever the app shows a recipe's weights, it SHALL show a total bar followed by one row per
ingredient: Flour, then Water, then the recipe's other ingredients in the recipe's order. The total
bar SHALL read "TOTAL DOUGH", show the portions and portion weight as "<portions> × <weight> g", and
show the total dough weight in grams. Each ingredient row SHALL show the ingredient's name, its
percentage and its weight. Ingredients whose weight is 0 g SHALL be left out of the table. The
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

## ADDED Requirements

### Requirement: Percentage precision
Every percentage, water's included, SHALL have a precision of whole numbers, one decimal or two
decimals. The precision SHALL be set by the number of decimals the user types, counting trailing
zeros, up to two. A value typed with more than two decimals SHALL be rounded half up to two
decimals. The precision SHALL set the step for that percentage: 1, 0.1 or 0.01. The precision SHALL
stay with the ingredient until the user types a new value, including when the value is stepped to
0, and SHALL be kept when the recipe is saved, loaded or the app is restarted.

#### Scenario: Whole number steps by one
- **WHEN** the user types 60 for water
- **THEN** water's step is 1

#### Scenario: One decimal steps by a tenth
- **WHEN** the user types 0.7 for Yeast
- **THEN** Yeast's step is 0.1

#### Scenario: Trailing zero counts
- **WHEN** the user types 3.10 for Salt
- **THEN** Salt is 3.1% with a step of 0.01

#### Scenario: More than two decimals
- **WHEN** the user types 0.125 for Yeast
- **THEN** Yeast becomes 0.13% with a step of 0.01

#### Scenario: Precision survives reaching zero
- **WHEN** Yeast is 0.2% with a step of 0.1 and is stepped down to 0%
- **THEN** its step is still 0.1

### Requirement: Ingredient names
Every ingredient other than flour and water SHALL have a name of 1 to 30 characters. Leading and
trailing spaces SHALL be removed. Two ingredients in one recipe SHALL NOT have the same name,
ignoring upper and lower case. "Flour", "Water" and "Hydration" SHALL NOT be used as names, ignoring
upper and lower case.

#### Scenario: Same name in another case
- **WHEN** a recipe has "Salt" and the user names another ingredient "salt"
- **THEN** the name is refused

#### Scenario: Similar names are allowed
- **WHEN** a recipe has "Salt before" and the user names another ingredient "Salt after"
- **THEN** both are kept

#### Scenario: Reserved name
- **WHEN** the user names an ingredient "Water"
- **THEN** the name is refused
