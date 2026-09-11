package com.example.pizza.dough

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = "Pizzas",
                    subtitle = "Dough balls",
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
        StepButton(
            icon = R.drawable.ic_remove,
            contentDescription = "Fewer pizzas",
            enabled = count > 0,
            onClick = { onCountChange(count - 1) }
        )
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
        StepButton(
            icon = R.drawable.ic_add,
            contentDescription = "More pizzas",
            enabled = count < MAX_PIZZAS,
            onClick = { onCountChange(count + 1) }
        )
    }
}

@Composable
internal fun BakerPercentagesCard(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Ingredients", subtitle = "As a percentage of the flour weight")
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
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
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
        // The slider is for quick changes; the buttons fine-tune one step at a time.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepButton(
                icon = R.drawable.ic_remove,
                contentDescription = "Decrease $label",
                enabled = value > scale.range.start,
                onClick = { onValueChange(scale.stepBy(value, steps = -1)) }
            )
            Slider(
                value = value,
                onValueChange = { onValueChange(scale.snap(it)) },
                valueRange = scale.range,
                modifier = Modifier.weight(1f)
            )
            StepButton(
                icon = R.drawable.ic_add,
                contentDescription = "Increase $label",
                enabled = value < scale.range.endInclusive,
                onClick = { onValueChange(scale.stepBy(value, steps = 1)) }
            )
        }
    }
}

/** The one −/+ button style, shared by the pizza count and every slider. */
@Composable
private fun StepButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(painter = painterResource(icon), contentDescription = contentDescription)
    }
}

/** A continuous slider range whose values snap to [step] without drawing a tick per step. */
private data class SliderScale(val range: ClosedFloatingPointRange<Float>, val step: Float) {
    fun snap(value: Float) = ((value / step).roundToInt() * step).coerceIn(range)

    fun stepBy(value: Float, steps: Int) = snap(value + steps * step)
}
