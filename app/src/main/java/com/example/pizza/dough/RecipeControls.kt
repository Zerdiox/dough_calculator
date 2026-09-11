package com.example.pizza.dough

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizza.R
import kotlin.math.roundToInt

private const val MAX_PIZZAS = 50

private val BallWeightScale = SliderScale(range = 100f..500f, step = 10f)
private val HydrationScale = SliderScale(range = 50f..90f, step = 1f)
private val SaltScale = SliderScale(range = 0f..5f, step = 0.1f)
private val YeastScale = SliderScale(range = 0f..3f, step = 0.05f)
private val OilScale = SliderScale(range = 0f..6f, step = 0.5f)

@Composable
internal fun PizzaSizeCard(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = "Pizzas",
                    subtitle = "Number of dough balls",
                    modifier = Modifier.weight(1f)
                )
                PizzaCountStepper(
                    count = recipe.pizzaCount,
                    onCountChange = { onRecipeChange(recipe.copy(pizzaCount = it)) }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            LabeledSlider(
                label = "Dough ball weight",
                valueText = "${recipe.ballWeightGrams} g",
                value = recipe.ballWeightGrams.toFloat(),
                scale = BallWeightScale,
                onValueChange = { onRecipeChange(recipe.copy(ballWeightGrams = it.roundToInt())) }
            )
        }
    }
}

@Composable
private fun PizzaCountStepper(
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(onClick = { onCountChange(count - 1) }, enabled = count > 0) {
            Icon(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = "Fewer pizzas"
            )
        }
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInVertically { height -> direction * height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -direction * height } + fadeOut())
            },
            label = "pizzaCount"
        ) { value ->
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 56.dp)
            )
        }
        FilledTonalIconButton(onClick = {
            onCountChange(count + 1)
        }, enabled = count < MAX_PIZZAS) {
            Icon(painter = painterResource(R.drawable.ic_add), contentDescription = "More pizzas")
        }
    }
}

@Composable
internal fun BakerPercentagesCard(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Baker's percentages", subtitle = "Relative to the flour weight")
            PercentSlider(
                label = "Hydration",
                value = recipe.hydrationPercent,
                scale = HydrationScale,
                onValueChange = { onRecipeChange(recipe.copy(hydrationPercent = it)) }
            )
            PercentSlider(
                label = "Salt",
                value = recipe.saltPercent,
                scale = SaltScale,
                onValueChange = { onRecipeChange(recipe.copy(saltPercent = it)) }
            )
            PercentSlider(
                label = "Yeast",
                value = recipe.yeastPercent,
                scale = YeastScale,
                onValueChange = { onRecipeChange(recipe.copy(yeastPercent = it)) }
            )
            PercentSlider(
                label = "Olive oil",
                value = recipe.oilPercent,
                scale = OilScale,
                onValueChange = { onRecipeChange(recipe.copy(oilPercent = it)) }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PercentSlider(
    label: String,
    value: Float,
    scale: SliderScale,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    LabeledSlider(
        label = label,
        valueText = "${value.formatDecimals(2)}%",
        value = value,
        scale = scale,
        onValueChange = onValueChange,
        modifier = modifier
    )
}

@Composable
private fun LabeledSlider(
    label: String,
    valueText: String,
    value: Float,
    scale: SliderScale,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = { onValueChange(scale.snap(it)) },
            valueRange = scale.range
        )
    }
}

/** A continuous slider range whose values snap to [step] without drawing a tick per step. */
private data class SliderScale(val range: ClosedFloatingPointRange<Float>, val step: Float) {
    fun snap(value: Float) = ((value / step).roundToInt() * step).coerceIn(range)
}
