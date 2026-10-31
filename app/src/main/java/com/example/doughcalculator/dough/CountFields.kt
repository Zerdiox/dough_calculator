package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

/** The choice between measuring an ingredient as [otherLabel] or counting it. */
@Composable
internal fun CountChoice(
    otherLabel: String,
    isCount: Boolean,
    onIsCountChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf(otherLabel, "Count")
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        labels.forEachIndexed { index, label ->
            val forCount = label != otherLabel
            SegmentedButton(
                selected = forCount == isCount,
                onClick = { onIsCountChange(forCount) },
                shape = SegmentedButtonDefaults.itemShape(index, labels.size)
            ) {
                Text(label)
            }
        }
    }
}

/** The optional weight of one of a count ingredient named [displayName]. */
@Composable
internal fun GramsEachRow(
    displayName: String,
    gramsEach: String,
    error: String?,
    onGramsEachChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Grams each",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            GramsField(
                grams = gramsEach,
                description = "Grams each of $displayName",
                isError = error != null,
                onGramsChange = onGramsEachChange
            )
        }
        FieldError(error)
    }
}

/** A weight typed in grams, with [unit] after it and a keyboard for decimals. */
@Composable
internal fun GramsField(
    grams: String,
    description: String,
    isError: Boolean,
    onGramsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    unit: String = "g"
) {
    val focusManager = LocalFocusManager.current
    CompactTextField(
        value = grams,
        onValueChange = onGramsChange,
        // The row's name is a separate text, so the field carries it for screen readers.
        modifier = modifier
            .width(FieldWidth)
            .semantics { contentDescription = description },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        suffix = unit.takeIf { it.isNotEmpty() }?.let { { Text(it) } },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true
    )
}

/** Why a weight can't be used, under its row, where a long message has room. */
@Composable
internal fun FieldError(error: String?, modifier: Modifier = Modifier) {
    if (error.isNullOrEmpty()) return
    Text(
        text = error,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.End,
        modifier = modifier.fillMaxWidth()
    )
}
