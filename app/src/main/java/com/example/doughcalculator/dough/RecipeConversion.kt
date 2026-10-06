package com.example.doughcalculator.dough

import java.math.BigDecimal
import java.math.RoundingMode

// Up to six whole digits and two decimals; rules out signs and exponents BigDecimal would accept.
private val GRAMS = Regex("""\d{1,6}(\.\d{0,2})?|\.\d{1,2}""")
private val ENTER_GRAMS = Parsed.Error("Enter grams")
private val NO_FLOUR = Parsed.Error("More than 0 g")
private val OVER_MAXIMUM = Parsed.Error("Over $MAX_PERCENT% of the flour")
private const val PORTIONS_TOO_SMALL = "Portions under $MIN_GRAMS g"
private val ZERO_PERCENT = Percentage(hundredths = 0, decimals = 0)

/**
 * A recipe typed in grams, to be turned into baker's percentages with [toRecipe]. Weights hold the
 * text as typed, so a half-typed weight stays on screen.
 */
data class ConversionDraft(
    val portionCount: Int,
    val flour: String = "",
    val water: String = "",
    val rows: ConvertedRows = ConvertedRows(),
    /** Whether saving keeps the screen open for the next recipe. */
    val addMore: Boolean = false
) {
    /** Why the flour weight can't be used, or null when it can or is still blank. */
    val flourError: String? get() = flourWeight.errorMessage

    /** Why the water weight can't be used, or null when it can or is still blank. */
    val waterError: String? get() = percentageOf(water).errorMessage

    /** Why the portions are too small, or null when they aren't or the weights are unfinished. */
    val portionsError: String?
        get() = portionWeight?.takeIf { it < MIN_GRAMS }?.let { PORTIONS_TOO_SMALL }

    /** An empty draft for the next recipe, keeping the portions and the "Add more" tick. */
    fun clearedForNext(): ConversionDraft = ConversionDraft(portionCount, addMore = addMore)

    /** Why the weight of row [key] can't be used, or null when it can or is still blank. */
    fun rowGramsError(key: Int): String? =
        rows.items.firstOrNull { it.key == key }?.let { percentageOf(it.grams).errorMessage }

    /** The recipe in baker's percentages, or null while anything blocks the conversion. */
    fun toRecipe(): DoughRecipe? {
        val portionWeight = portionWeight?.takeIf { it >= MIN_GRAMS }
        // Blank water counts as none.
        val hydration = percentageOf(water) ?: Parsed.Value(ZERO_PERCENT)
        val ingredients = ingredients()
        if (portionWeight == null || hydration !is Parsed.Value || ingredients == null) return null
        return DoughRecipe(
            portionCount = portionCount,
            portionWeightGrams = portionWeight,
            hydration = hydration.value,
            ingredients = ingredients
        )
    }

    /**
     * The other ingredients as percentages, or null while a name or weight can't be used. Rows
     * without a weight are left out; one that rounds to 0% is kept.
     */
    private fun ingredients(): List<Ingredient>? {
        if (!rows.namesAreValid) return null
        val ingredients = rows.items.filter { gramsOf(it.grams) > BigDecimal.ZERO }.map { row ->
            (percentageOf(row.grams) as? Parsed.Value)?.let {
                Ingredient(row.name.trim(), it.value)
            }
        }
        return ingredients.takeIf { null !in it }?.filterNotNull()
    }

    /** The flour weight, or why it can't be used; null while blank. */
    private val flourWeight: Parsed<BigDecimal>?
        get() = readGrams(flour)?.let { grams ->
            if ((grams as? Parsed.Value)?.value?.signum() == 0) NO_FLOUR else grams
        }

    private val usableFlourWeight: BigDecimal? get() = (flourWeight as? Parsed.Value)?.value

    /** The total weight divided over the portions, rounded down, or null while unfinished. */
    private val portionWeight: Int?
        get() {
            val flourWeight = usableFlourWeight
            val others = listOf(water) + rows.items.map { it.grams }
            if (flourWeight == null || others.any { readGrams(it) is Parsed.Error }) return null
            val total = others.fold(flourWeight) { sum, text -> sum + gramsOf(text) }
            return total.divide(BigDecimal(portionCount), 0, RoundingMode.FLOOR).toInt()
        }

    /** The share of the flour that [text] grams make, or why it can't be; null while unknown. */
    private fun percentageOf(text: String): Parsed<Percentage>? =
        when (val grams = readGrams(text)) {
            null -> null
            is Parsed.Error -> grams
            is Parsed.Value -> usableFlourWeight?.let { percentage(grams.value, it) }
        }
}

/** A row for an ingredient besides flour and water, with its weight as typed. */
data class ConvertedRow(val key: Int, val name: String, val grams: String) {
    /** The name as messages and screen readers say it, standing in for a blank one. */
    val displayName: String get() = displayNameOf(name)
}

/** The rows for ingredients besides flour and water on the Convert screen. */
@ConsistentCopyVisibility
data class ConvertedRows private constructor(
    val items: List<ConvertedRow>,
    /** The latest removed row, which Undo puts back; a newer removal replaces it. */
    val lastRemoval: Removal<ConvertedRow>?,
    private val nextKey: Int
) {
    constructor() : this(items = emptyList(), lastRemoval = null, nextKey = 0)

    /** Whether every row's name can be used. */
    val namesAreValid: Boolean get() = items.all { nameError(it.key) == null }

    /** Adds a row with an empty name and weight at the end. */
    fun add(): ConvertedRows =
        copy(items = items + ConvertedRow(nextKey, name = "", grams = ""), nextKey = nextKey + 1)

    fun rename(key: Int, name: String): ConvertedRows = change(key) { it.copy(name = name) }

    fun changeGrams(key: Int, text: String): ConvertedRows = change(key) { it.copy(grams = text) }

    fun remove(key: Int): ConvertedRows {
        val index = items.indexOfFirst { it.key == key }
        if (index < 0) return this
        return copy(items = items - items[index], lastRemoval = Removal(items[index], index))
    }

    fun undoRemove(): ConvertedRows {
        val removal = lastRemoval ?: return this
        return copy(items = restore(items, removal), lastRemoval = null)
    }

    /** Ends the chance to undo the latest removal, once its message has gone. */
    fun clearRemoval(): ConvertedRows = copy(lastRemoval = null)

    /**
     * Why the name of row [key] can't be used, or null when it can. A name repeated from an earlier
     * row is flagged on the later one, so the row the user is typing in shows the message.
     */
    fun nameError(key: Int): String? {
        val index = items.indexOfFirst { it.key == key }
        if (index < 0) return null
        return ingredientNameError(items[index].name, items.take(index).map { it.name })
    }

    /** The saved names to offer for row [key], leaving out those used by other rows. */
    fun offeredNames(key: Int, savedNames: List<String>): List<String> {
        val row = items.firstOrNull { it.key == key } ?: return emptyList()
        return namesToOffer(row.name, items.filter { it.key != key }.map { it.name }, savedNames)
    }

    private fun change(key: Int, change: (ConvertedRow) -> ConvertedRow): ConvertedRows =
        copy(items = items.map { if (it.key == key) change(it) else it })
}

/** What [text] says in grams, or null while it is blank. A comma counts as a decimal point. */
private fun readGrams(text: String): Parsed<BigDecimal>? {
    val typed = text.trim().replace(',', '.')
    return when {
        typed.isEmpty() -> null
        GRAMS.matches(typed) -> Parsed.Value(BigDecimal(typed))
        else -> ENTER_GRAMS
    }
}

/** The grams in [text], counting blank or unusable text as none. */
private fun gramsOf(text: String): BigDecimal =
    (readGrams(text) as? Parsed.Value)?.value ?: BigDecimal.ZERO

/**
 * [weight] as a percentage of [flourWeight], rounded half up to hundredths, with as few decimals
 * as the rounded value needs so it sits on its own step.
 */
private fun percentage(weight: BigDecimal, flourWeight: BigDecimal): Parsed<Percentage> {
    val hundredths = (weight * BigDecimal(FULL_PERCENT_HUNDREDTHS))
        .divide(flourWeight, 0, RoundingMode.HALF_UP)
    if (hundredths > BigDecimal(MAX_PERCENT_HUNDREDTHS)) return OVER_MAXIMUM
    val decimals = hundredths.movePointLeft(MAX_PERCENT_DECIMALS)
        .stripTrailingZeros()
        .scale()
        .coerceIn(0, MAX_PERCENT_DECIMALS)
    return Parsed.Value(Percentage(hundredths.intValueExact(), decimals))
}

private val Parsed<*>?.errorMessage: String? get() = (this as? Parsed.Error)?.message
