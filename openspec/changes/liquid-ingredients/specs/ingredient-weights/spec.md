## ADDED Requirements

### Requirement: Liquid ingredients
Every ingredient other than flour SHALL be one of three kinds: percentage, liquid or count. A liquid
SHALL be measured as a percentage of the flour weight and SHALL follow every rule for percentage
ingredients. No ingredient SHALL be liquid because of its name.

#### Scenario: Name does not make a liquid
- **WHEN** the user adds an ingredient named "Water" and leaves it set to "%"
- **THEN** it is not liquid

#### Scenario: Liquid does not change the weights
- **WHEN** a recipe has Beer 65% and the user changes Beer to Liquid
- **THEN** every weight stays the same

### Requirement: Hydration
A recipe's hydration SHALL be the sum of the percentages of its liquid ingredients. With no liquid
ingredients it SHALL be 0%. It MAY be above 200%. Wherever it is shown, it SHALL have at most one
decimal, rounded half up, with no trailing zeros.

#### Scenario: Several liquids
- **WHEN** a recipe has Water 25%, Beer 40% and Salt 3%, with Water and Beer liquid
- **THEN** its hydration is 65%

#### Scenario: No liquids
- **WHEN** a recipe has Salt 3% and nothing is liquid
- **THEN** its hydration is 0%

#### Scenario: Rounded to one decimal
- **WHEN** a recipe's liquids add up to 62.25%
- **THEN** its hydration is shown as 62.3%

### Requirement: Liquids come first
Wherever a recipe's ingredients are listed, liquid ingredients SHALL come before all others. Within
each of the two groups, ingredients SHALL keep the order the user gave them.

#### Scenario: Liquids before the rest
- **WHEN** a recipe has Salt, Beer, Yeast and Water, in that order, with Beer and Water liquid
- **THEN** wherever the recipe is shown, they appear as Beer, Water, Salt, Yeast

## MODIFIED Requirements

### Requirement: Ingredient list
A recipe SHALL be made of flour and any number of other ingredients. Flour SHALL always be 100% and
SHALL NOT be adjustable. Every other ingredient SHALL have a name and either a percentage of the
flour weight from 0% to 200% or a count, and SHALL keep the place in the recipe the user gave it
within its group.

A new recipe SHALL have flour and no other ingredients.

#### Scenario: Fresh recipe uses the default percentages
- **WHEN** a recipe is created without any saved values
- **THEN** it has flour at 100%
- **AND** no other ingredients

#### Scenario: Other ingredients keep their order
- **WHEN** a recipe has Salt, Yeast and Honey, in that order, and none is liquid
- **THEN** wherever the recipe is shown, they appear as Salt, Yeast, Honey

#### Scenario: Very wet dough
- **WHEN** the user sets Water to 110%
- **THEN** the recipe has Water at 110% and the weights follow

#### Scenario: Percentage stays on a step within its range
- **WHEN** the user tries to set an ingredient above 200%
- **THEN** the percentage is not accepted and the previous value stays

### Requirement: Weights from baker's percentages
The weight of a count ingredient with grams each SHALL be its current amount multiplied by its grams
each. The flour weight SHALL be the total dough weight, less the weight of those count ingredients,
divided by one plus the sum of the percentages of every percentage ingredient (as fractions). The
weight of every percentage ingredient SHALL be the flour weight multiplied by its percentage. All
these weights SHALL add up to the total dough weight. A count ingredient without grams each SHALL
have no weight and SHALL NOT affect any other weight.

When count ingredients weigh as much as the total dough weight or more, flour and the percentage
ingredients SHALL be 0 g, and the weights table SHALL say "Counted ingredients use up the whole
dough weight".

#### Scenario: Weights for a 1000 g dough
- **WHEN** a recipe totals 1000 g with Water 65%, Salt 3% and Yeast 0.2%
- **THEN** flour is about 594.5 g (1000 / 1.682)
- **AND** Water is 65%, Salt 3% and Yeast 0.2% of that flour weight
- **AND** all weights together add up to 1000 g

#### Scenario: Only flour
- **WHEN** a recipe totals 1000 g and has no other ingredients
- **THEN** flour is 1000 g

#### Scenario: More portions scale every ingredient
- **WHEN** the number of portions is doubled
- **THEN** every ingredient weight doubles

#### Scenario: Count with grams each
- **WHEN** a recipe totals 1000 g with Water 60% and Eggs at 2 with 50 g each
- **THEN** Eggs weighs 100 g and flour is 562.5 g (900 / 1.6)
- **AND** all weights together add up to 1000 g

#### Scenario: Count without grams each
- **WHEN** a recipe totals 1000 g with Water 60% and Eggs at 2 with no grams each
- **THEN** flour is 625 g (1000 / 1.6), the same as without Eggs

#### Scenario: Count ingredients too heavy
- **WHEN** a recipe totals 100 g and Eggs is at 4 with 50 g each
- **THEN** flour and the percentage ingredients are 0 g
- **AND** the weights table says "Counted ingredients use up the whole dough weight"

### Requirement: Weights table
Wherever the app shows a recipe's weights, it SHALL show a total bar followed by one row per
ingredient: Flour, then the recipe's other ingredients in the recipe's order. The total bar SHALL
read "TOTAL DOUGH", show the portions and portion weight as "<portions> × <weight> g", and show the
total dough weight in grams. Each percentage row SHALL show the ingredient's name, its percentage
and its weight. Each count row SHALL show the ingredient's name and its amount as "× <amount>" with
no trailing zeros, followed by its weight only when it has grams each. Percentage ingredients whose
weight is 0 g and count ingredients at 0 SHALL be left out of the table. The weights SHALL update as
soon as any input changes.

#### Scenario: Table for the default recipe
- **WHEN** the table shows 4 portions of 250 g with the default recipe
- **THEN** the total bar reads "TOTAL DOUGH", "4 × 250 g" and "1000 g"
- **AND** only a row for Flour follows

#### Scenario: Other ingredients follow water in recipe order
- **WHEN** a recipe has Salt 3%, Water 60% and Yeast 0.2%, in that order, with Water liquid
- **THEN** the rows read Flour, Water, Salt, Yeast

#### Scenario: Unused ingredient is hidden
- **WHEN** an ingredient is at 0%, Water included
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

### Requirement: Percentage precision
Every percentage SHALL have a precision of whole numbers, one decimal or two decimals. The precision
SHALL be set by the number of decimals the user types, counting trailing zeros, up to two. A value
typed with more than two decimals SHALL be rounded half up to two decimals. The precision SHALL set
the step for that percentage: 1, 0.1 or 0.01. The precision SHALL stay with the ingredient until the
user types a new value, including when the value is stepped to 0, and SHALL be kept when the recipe
is saved, loaded or the app is restarted.

#### Scenario: Whole number steps by one
- **WHEN** the user types 60 for Water
- **THEN** Water's step is 1

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
Every ingredient other than flour SHALL have a name of 1 to 30 characters. Leading and trailing
spaces SHALL be removed. Two ingredients in one recipe SHALL NOT have the same name, ignoring upper
and lower case. "Flour" and "Hydration" SHALL NOT be used as names, ignoring upper and lower case.

#### Scenario: Same name in another case
- **WHEN** a recipe has "Salt" and the user names another ingredient "salt"
- **THEN** the name is refused

#### Scenario: Similar names are allowed
- **WHEN** a recipe has "Salt before" and the user names another ingredient "Salt after"
- **THEN** both are kept

#### Scenario: Reserved name
- **WHEN** the user names an ingredient "Flour" or "Hydration"
- **THEN** the name is refused

#### Scenario: Water is an ordinary name
- **WHEN** the user names an ingredient "Water"
- **THEN** the name is accepted

### Requirement: Count ingredients
A count ingredient SHALL have an amount from 0 to 100 in steps of 0.5, for the number of portions it was set
at, and optionally a weight in grams for each one. When the portions change, the amount SHALL scale
in proportion and be rounded half up to the nearest 0.5. An amount above 0 SHALL never scale below
0.5. Going back to the portions it was set at SHALL give the amount that was set.

#### Scenario: Count follows the portions
- **WHEN** Eggs is set to 2 at 4 portions
- **THEN** Eggs is 0.5 at 1 portion, 1 at 2 portions, 1.5 at 3 portions and 3 at 6 portions

#### Scenario: Never below a half
- **WHEN** Eggs is set to 1 at 10 portions and the portions become 1
- **THEN** Eggs is 0.5

#### Scenario: Back to the original portions
- **WHEN** Eggs is set to 2 at 4 portions, and the portions go to 3 and back to 4
- **THEN** Eggs is 2
