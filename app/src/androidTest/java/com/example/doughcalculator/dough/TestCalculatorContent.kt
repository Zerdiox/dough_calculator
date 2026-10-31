package com.example.doughcalculator.dough

import androidx.compose.runtime.Composable

/** The calculator with nothing loaded and no actions, for a test to set only what it looks at. */
@Composable
internal fun TestCalculatorContent(
    recipe: DoughRecipe = DefaultDoughRecipe,
    loadedRecipeName: String? = null,
    ingredientEditor: IngredientListEditor? = null,
    savedIngredients: List<SavedIngredient> = emptyList(),
    resetMessagePending: Boolean = false,
    onRecipeChange: (DoughRecipe) -> Unit = {},
    onStartEditing: () -> Unit = {},
    onEditorChange: (IngredientListEditor) -> Unit = {},
    onFinishEditing: () -> Unit = {},
    onSaveRecipe: (name: String, namesToSave: Set<String>) -> Unit = { _, _ -> },
    onUpdateLoadedRecipe: (namesToSave: Set<String>) -> Unit = {},
    onDismissResetMessage: () -> Unit = {}
) {
    DoughCalculatorContent(
        recipe = recipe,
        loadedRecipeName = loadedRecipeName,
        ingredientEditor = ingredientEditor,
        savedIngredients = savedIngredients,
        resetMessagePending = resetMessagePending,
        onRecipeChange = onRecipeChange,
        onStartEditing = onStartEditing,
        onEditorChange = onEditorChange,
        onFinishEditing = onFinishEditing,
        onSaveRecipe = onSaveRecipe,
        onUpdateLoadedRecipe = onUpdateLoadedRecipe,
        onOpenSavedRecipes = {},
        onOpenConvert = {},
        onOpenFullRecipe = {},
        onDismissResetMessage = onDismissResetMessage
    )
}
