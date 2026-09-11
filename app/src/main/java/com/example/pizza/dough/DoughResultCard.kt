package com.example.pizza.dough

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.math.RoundingMode

private const val FLOUR_PERCENT = 100f
private const val SMALL_AMOUNT_GRAMS = 10f

// Tiny amounts (yeast) still get a visible sliver in the composition bar.
private const val MIN_SEGMENT_FRACTION = 0.01f

/**
 * The hero card: total dough weight plus a breakdown per ingredient.
 * [footer] is shown below the ingredients, for example an action button.
 */
@Composable
internal fun DoughResultCard(
    recipe: DoughRecipe,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {}
) {
    val ingredients = recipe.ingredients().filter { it.grams > 0f }
    val totalGrams by animateIntAsState(targetValue = recipe.totalDoughGrams, label = "totalDough")
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(text = "Total dough", style = MaterialTheme.typography.labelLarge)
                Text(text = "$totalGrams g", style = MaterialTheme.typography.displayMedium)
                Text(
                    text = "${recipe.pizzaCount} × ${recipe.ballWeightGrams} g",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (ingredients.isNotEmpty()) {
                CompositionBar(
                    ingredients = ingredients,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            ingredients.forEach { ingredient ->
                key(ingredient.name) {
                    IngredientRow(ingredient = ingredient)
                }
            }
            footer()
        }
    }
}

/** A horizontal bar where each ingredient takes a share proportional to its weight. */
@Composable
private fun CompositionBar(ingredients: List<Ingredient>, modifier: Modifier = Modifier) {
    val minWeight = ingredients.sumOf { it.grams.toDouble() }.toFloat() * MIN_SEGMENT_FRACTION
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        ingredients.forEach { ingredient ->
            key(ingredient.name) {
                val weight by animateFloatAsState(
                    targetValue = ingredient.grams.coerceAtLeast(minWeight),
                    label = "${ingredient.name} share"
                )
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxHeight()
                        .background(ingredient.color)
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient, modifier: Modifier = Modifier) {
    val grams by animateFloatAsState(
        targetValue = ingredient.grams,
        label = "${ingredient.name} grams"
    )
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(ingredient.color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${ingredient.percent.formatDecimals(2)}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = formatGrams(grams),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 88.dp)
        )
    }
}

private data class Ingredient(
    val name: String,
    val grams: Float,
    val percent: Float,
    val color: Color
)

@Composable
@ReadOnlyComposable
private fun DoughRecipe.ingredients(): List<Ingredient> {
    val amounts = amounts()
    val colors = MaterialTheme.colorScheme
    return listOf(
        Ingredient(
            name = "Flour",
            grams = amounts.flour,
            percent = FLOUR_PERCENT,
            color = colors.primary
        ),
        Ingredient(
            name = "Water",
            grams = amounts.water,
            percent = hydrationPercent,
            color = colors.tertiary
        ),
        Ingredient(
            name = "Salt",
            grams = amounts.salt,
            percent = saltPercent,
            color = colors.secondary
        ),
        Ingredient(
            name = "Yeast",
            grams = amounts.yeast,
            percent = yeastPercent,
            color = colors.onPrimaryContainer
        ),
        Ingredient(
            name = "Olive oil",
            grams = amounts.oil,
            percent = oilPercent,
            color = colors.outline
        )
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
