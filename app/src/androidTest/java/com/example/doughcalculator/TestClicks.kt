package com.example.doughcalculator

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performSemanticsAction

/**
 * Clicks without touching the screen. After typing, the keyboard is still moving things around, so
 * a touch at where a control was a moment ago can miss it.
 */
internal fun SemanticsNodeInteraction.clickDirectly() =
    performSemanticsAction(SemanticsActions.OnClick)
