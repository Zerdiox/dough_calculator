package com.example.doughcalculator.dough

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.doughcalculator.R

private const val MIN_PORTIONS = 1
private const val MAX_PORTIONS = 50

@Composable
internal fun PortionsCard(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = "Portions",
                    subtitle = "How many, and how heavy",
                    modifier = Modifier.weight(1f)
                )
                PortionStepper(
                    count = recipe.portionCount,
                    onCountChange = { onRecipeChange(recipe.copy(portionCount = it)) }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            ValueStepper(
                label = "Portion weight",
                value = recipe.portionWeightGrams,
                rules = GramsRules,
                onValueChange = { onRecipeChange(recipe.copy(portionWeightGrams = it)) }
            )
        }
    }
}

@Composable
private fun PortionStepper(
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        StepButton(
            icon = R.drawable.ic_remove,
            contentDescription = "Fewer portions",
            enabled = count > MIN_PORTIONS,
            onClick = { onCountChange(count - 1) }
        )
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInVertically { height -> direction * height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -direction * height } + fadeOut())
            },
            label = "portionCount"
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
            contentDescription = "More portions",
            enabled = count < MAX_PORTIONS,
            onClick = { onCountChange(count + 1) }
        )
    }
}

@Composable
internal fun SectionHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** The one −/+ button style, shared by every control that steps a value. */
@Composable
internal fun StepButton(
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
