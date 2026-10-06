package com.example.doughcalculator.dough

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Whether an ingredient list is being edited, and the draft while it is. [onFinish] receives the
 * recipe with the edited list once editing ends.
 */
class IngredientEditing(private val onFinish: (DoughRecipe) -> Unit) {
    private val mutableEditor = MutableStateFlow<IngredientListEditor?>(null)

    /** The draft, or `null` when nothing is being edited. */
    val editor: StateFlow<IngredientListEditor?> = mutableEditor.asStateFlow()

    fun start(ingredients: List<Ingredient>) {
        mutableEditor.value = IngredientListEditor(ingredients)
    }

    fun change(editor: IngredientListEditor) {
        // A change arriving after Done must not start a new edit.
        mutableEditor.update { it?.let { editor } }
    }

    /**
     * Ends editing and passes [recipe], the calculator's recipe as it is now, with the edited list
     * to [onFinish]. Does nothing while any name in the list can't be used.
     */
    fun finish(recipe: DoughRecipe) {
        val ingredients = mutableEditor.value?.toIngredients() ?: return
        mutableEditor.value = null
        onFinish(recipe.copy(ingredients = ingredients))
    }
}
