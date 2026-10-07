package com.example.doughcalculator.dough

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

private const val LABEL_COLUMN_FRACTION = 0.46f
private val RowHeight = 48.dp

/** The recipe card: the total on a tomato header bar above a table of ingredient weights. */
@Composable
internal fun DoughResultCard(recipe: DoughRecipe, modifier: Modifier = Modifier) {
    val rows = weightRows(recipe)
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Column(modifier = Modifier.animateContentSize()) {
            TotalBar(recipe = recipe)
            rows.forEachIndexed { index, row ->
                key(row.name) {
                    if (index > 0) HorizontalDivider()
                    IngredientRow(row = row)
                }
            }
        }
    }
}

/** Stands in the weights table's place while there is no flour weight to work from. */
@Composable
internal fun FlourNeededCard(modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Text(
            text = "Enter the flour weight to see the recipe",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        )
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
private fun IngredientRow(row: WeightRow, modifier: Modifier = Modifier) {
    val target = row.grams.toFloat()
    val grams by animateFloatAsState(targetValue = target, label = "${row.name} grams")
    // Once settled, show the exact weight's text: the Float could round the other way.
    val gramsText = if (grams == target) row.gramsText else formatGrams(grams.toDouble())
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
                text = row.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = row.percentText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Text(
            text = gramsText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
