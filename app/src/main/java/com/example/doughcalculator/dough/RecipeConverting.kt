package com.example.doughcalculator.dough

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The recipe being typed on the Convert screen. Each visit starts from an empty draft. */
class RecipeConverting {
    private val mutableDraft = MutableStateFlow(ConversionDraft(DefaultDoughRecipe.portionCount))

    val draft: StateFlow<ConversionDraft> = mutableDraft.asStateFlow()

    /** Starts an empty draft with the calculator's [portionCount]. */
    fun start(portionCount: Int) {
        mutableDraft.value = ConversionDraft(portionCount)
    }

    fun change(draft: ConversionDraft) {
        mutableDraft.value = draft
    }
}
