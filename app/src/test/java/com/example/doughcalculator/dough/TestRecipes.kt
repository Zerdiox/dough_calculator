package com.example.doughcalculator.dough

/** 4 portions of 250 g at 65% water, with Salt 3.0% and Yeast 0.20%. */
internal val SaltAndYeastRecipe = DoughRecipe(
    portionCount = 4,
    portionWeightGrams = 250,
    hydration = Percentage(hundredths = 6500, decimals = 0),
    ingredients = listOf(
        Ingredient(name = "Salt", percentage = Percentage(hundredths = 300, decimals = 1)),
        Ingredient(name = "Yeast", percentage = Percentage(hundredths = 20, decimals = 2))
    )
)
