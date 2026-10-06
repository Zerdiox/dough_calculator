package com.example.doughcalculator.dough

/**
 * A row of the ingredient list while it is edited. [key] tells rows apart while their names change,
 * and lives only as long as the edit. [isAdded] marks rows added in this edit.
 */
data class EditedIngredient(
    val key: Int,
    val name: String,
    val percentage: Percentage,
    val isAdded: Boolean
) {
    /** The name as messages and screen readers say it, standing in for a blank one. */
    val displayName: String get() = displayNameOf(name)
}

/** [name] as messages and screen readers say it, standing in for a blank one. */
internal fun displayNameOf(name: String): String = name.trim().ifEmpty { "new ingredient" }

/** A removed row and where it was, so Undo can put it back. */
data class Removal<T>(val row: T, val index: Int)

/** [rows] with [removal]'s row back where it was, or last if the list has since become shorter. */
internal fun <T> restore(rows: List<T>, removal: Removal<T>): List<T> =
    rows.toMutableList().apply { add(removal.index.coerceAtMost(size), removal.row) }

/**
 * The [savedNames] to offer for a row holding [typed]: those not in [usedNames], containing what
 * the row holds so far, alphabetically. Case is ignored throughout.
 */
internal fun namesToOffer(
    typed: String,
    usedNames: List<String>,
    savedNames: List<String>
): List<String> = savedNames
    .filter { saved ->
        usedNames.none { it.isSameNameAs(saved) } && saved.contains(typed.trim(), ignoreCase = true)
    }
    .sortedWith(String.CASE_INSENSITIVE_ORDER)

/**
 * A draft of a recipe's ingredient list: rename, reorder, add and remove rows, then take the
 * result with [toIngredients]. Every change returns a new editor.
 */
@ConsistentCopyVisibility
data class IngredientListEditor private constructor(
    val rows: List<EditedIngredient>,
    /** The latest removal, which Undo reverts; a newer removal replaces it. */
    val lastRemoval: Removal<EditedIngredient>?,
    private val nextKey: Int
) {
    constructor(ingredients: List<Ingredient>) : this(
        rows = ingredients.mapIndexed { index, ingredient ->
            EditedIngredient(index, ingredient.name, ingredient.percentage, isAdded = false)
        },
        lastRemoval = null,
        nextKey = ingredients.size
    )

    fun rename(key: Int, name: String): IngredientListEditor =
        copy(rows = rows.map { if (it.key == key) it.copy(name = name) else it })

    fun move(fromIndex: Int, toIndex: Int): IngredientListEditor =
        copy(rows = rows.toMutableList().apply { add(toIndex, removeAt(fromIndex)) })

    /** Adds an unnamed row at the end, at 0% stepping by whole percents. */
    fun add(): IngredientListEditor = copy(
        rows = rows + EditedIngredient(nextKey, name = "", NEW_PERCENTAGE, isAdded = true),
        nextKey = nextKey + 1
    )

    fun remove(key: Int): IngredientListEditor {
        val index = rows.indexOfFirst { it.key == key }
        if (index < 0) return this
        return copy(rows = rows - rows[index], lastRemoval = Removal(rows[index], index))
    }

    fun undoRemove(): IngredientListEditor {
        val removal = lastRemoval ?: return this
        return copy(rows = restore(rows, removal), lastRemoval = null)
    }

    /** Ends the chance to undo the latest removal, once its message has gone. */
    fun clearRemoval(): IngredientListEditor = copy(lastRemoval = null)

    /**
     * Why the name of row [key] can't be used, or null when it can. A name repeated from an earlier
     * row is flagged on the later one, so the row the user is typing in shows the message.
     */
    fun nameError(key: Int): String? {
        val index = rows.indexOfFirst { it.key == key }
        if (index < 0) return null
        return ingredientNameError(rows[index].name, rows.take(index).map { it.name })
    }

    /** Whether every name can be used, so the edit can be applied. */
    val canApply: Boolean get() = rows.all { nameError(it.key) == null }

    /** The edited list with trimmed names, or null while any name can't be used. */
    fun toIngredients(): List<Ingredient>? {
        if (!canApply) return null
        return rows.map { Ingredient(it.name.trim(), it.percentage) }
    }

    /**
     * The [savedNames] to offer for row [key]: those not used by another row, containing what the
     * row holds so far, alphabetically. Case is ignored throughout.
     */
    fun offeredNames(key: Int, savedNames: List<String>): List<String> {
        val row = rows.firstOrNull { it.key == key } ?: return emptyList()
        return namesToOffer(row.name, rows.filter { it.key != key }.map { it.name }, savedNames)
    }

    private companion object {
        val NEW_PERCENTAGE = Percentage(hundredths = 0, decimals = 0)
    }
}
