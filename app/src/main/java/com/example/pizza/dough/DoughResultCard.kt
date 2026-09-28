package com.example.pizza.dough

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.RoundingMode

private const val FLOUR_PERCENT = 100f
private const val SMALL_AMOUNT_GRAMS = 10f
private const val LABEL_COLUMN_FRACTION = 0.46f
private val RowHeight = 48.dp

/** The recipe card: the total on a tomato header bar above a table of ingredient weights. */
@Composable
internal fun DoughResultCard(recipe: DoughRecipe, modifier: Modifier = Modifier) {
    val ingredients = recipe.ingredients().filter { it.grams > 0f }
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Column(modifier = Modifier.animateContentSize()) {
            TotalBar(recipe = recipe)
            ingredients.forEachIndexed { index, ingredient ->
                key(ingredient.name) {
                    if (index > 0) HorizontalDivider()
                    IngredientRow(ingredient = ingredient)
                }
            }
        }
    }
}

@Composable
private fun TotalBar(recipe: DoughRecipe, modifier: Modifier = Modifier) {
    val totalGrams by animateIntAsState(targetValue = recipe.totalDoughGrams, label = "totalDough")
    val textColor = MaterialTheme.colorScheme.onPrimary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "TOTAL DOUGH",
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 1.sp,
                color = textColor.copy(alpha = 0.85f)
            )
            Text(
                text = "${recipe.portionCount} × ${recipe.portionWeightGrams} g",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.85f)
            )
        }
        Text(
            text = "$totalGrams g",
            style = MaterialTheme.typography.displaySmall,
            color = textColor
        )
    }
}

/** One table row: a wheat label cell, the baker's percentage, and the weight. */
@Composable
private fun IngredientRow(ingredient: Ingredient, modifier: Modifier = Modifier) {
    val grams by animateFloatAsState(
        targetValue = ingredient.grams,
        label = "${ingredient.name} grams"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = RowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(LABEL_COLUMN_FRACTION)
                .heightIn(min = RowHeight)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = "${ingredient.percent.formatDecimals(2)}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Text(
            text = formatGrams(grams),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

private data class Ingredient(val name: String, val grams: Float, val percent: Float)

private fun DoughRecipe.ingredients(): List<Ingredient> {
    val amounts = amounts()
    return listOf(
        Ingredient(name = "Flour", grams = amounts.flour, percent = FLOUR_PERCENT),
        Ingredient(name = "Water", grams = amounts.water, percent = hydrationPercent),
        Ingredient(name = "Salt", grams = amounts.salt, percent = saltPercent),
        Ingredient(name = "Yeast", grams = amounts.yeast, percent = yeastPercent),
        Ingredient(name = "Olive oil", grams = amounts.oil, percent = oilPercent)
    )
}

private fun formatGrams(grams: Float): String {
    val decimals = if (grams < SMALL_AMOUNT_GRAMS) 1 else 0
    return "${grams.formatDecimals(decimals)} g"
}

/** Rounds to at most [decimals] decimals and drops trailing zeros: 2.50 → "2.5", 62.0 → "62". */
internal fun Float.formatDecimals(decimals: Int): String = toBigDecimal()
    .setScale(decimals, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()
