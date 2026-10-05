package com.example.pizza.dough

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizza.R

// Wide enough for the longest values, "999999 g" and "100.00 %", inside the default padding.
private val FieldWidth = 112.dp

/**
 * A named value the user can type, or step with − and +, following [rules]. Only accepted values
 * reach [onValueChange]; while the typed text can't be used, the rule's message shows instead.
 */
@Composable
internal fun <T> ValueStepper(
    label: String,
    value: T,
    rules: ValueRules<T>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    // While the field has focus, its text is the user's: text half-way through typing ("0." or
    // "05") must survive, and a value reported a keystroke ago can arrive after the next one.
    // Without focus, the field shows the value.
    var typedText by remember { mutableStateOf<String?>(null) }
    val text = typedText ?: rules.fieldText(value)
    val error = rules.parse(text) as? Parsed.Error
    val step = { stepped: T ->
        if (typedText != null) typedText = rules.fieldText(stepped)
        onValueChange(stepped)
    }

    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        // Centred on the field's height rather than the whole row, which grows with the hint.
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = OutlinedTextFieldDefaults.MinHeight)
                .wrapContentHeight(Alignment.CenterVertically)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepButton(
                    icon = R.drawable.ic_remove,
                    contentDescription = "Decrease $label",
                    enabled = rules.canStepDown(value),
                    onClick = { step(rules.stepDown(value)) }
                )
                ValueField(
                    text = text,
                    onTextChange = { typed ->
                        if (rules.maxLength?.let { typed.length > it } == true) return@ValueField
                        typedText = typed
                        (rules.parse(typed) as? Parsed.Value)?.let { onValueChange(it.value) }
                    },
                    label = label,
                    unit = rules.unit,
                    acceptsDecimals = rules.acceptsDecimals,
                    isError = error != null,
                    modifier = Modifier.onFocusChanged {
                        typedText = if (it.isFocused) typedText ?: rules.fieldText(value) else null
                    }
                )
                StepButton(
                    icon = R.drawable.ic_add,
                    contentDescription = "Increase $label",
                    enabled = rules.canStepUp(value),
                    onClick = { step(rules.stepUp(value)) }
                )
            }
            StepperNotes(hint = rules.hintText(value), error = error?.message)
        }
    }
}

/** The step size under the field, and the reason the typed text can't be used, if any. */
@Composable
private fun StepperNotes(hint: String, error: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** The compact field: the text with [unit] after it, and a keyboard that fits the value. */
@Composable
private fun ValueField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    unit: String,
    acceptsDecimals: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        // The row's name is a separate text, so the field carries it for screen readers.
        modifier = modifier
            .width(FieldWidth)
            .semantics { contentDescription = label },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        suffix = { Text(unit) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (acceptsDecimals) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true
    )
}
