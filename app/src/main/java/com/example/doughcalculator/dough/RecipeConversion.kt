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
private const val GRAMS_DECIMALS = 2

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

    /** Why the amount of row [key] can't be used, or null when it can or is still blank. */
    fun rowGramsError(key: Int): String? = rows.items.firstOrNull { it.key == key }?.amountError

    /** Why the "Grams each" of row [key] can't be used, or null when it can or doesn't apply. */
    fun rowGramsEachError(key: Int): String? =
        rows.items.firstOrNull { it.key == key }?.gramsEachError

    /** The recipe in baker's percentages, or null while anything blocks the conversion. */
    fun toRecipe(): DoughRecipe? = preview()?.takeIf { isComplete }

    /**
     * The recipe made from what can be used so far, or null while the flour weight can't be used.
     * Rows whose name or amount can't be used are left out, as are rows without an amount; one that
     * rounds to 0% is kept. Water that can't be used counts as none. The portion weight has no
     * minimum.
     */
    fun preview(): DoughRecipe? {
        val flourWeight = usableFlourWeight ?: return null
        val hydration = usablePercentage(water)
        val usedRows = rows.items
            .filter { rows.nameError(it.key) == null }
            .mapNotNull { row -> usedIngredient(row)?.let { row to it } }
        val waterWeight = if (hydration != null) gramsOf(water) else BigDecimal.ZERO
        val total = usedRows.fold(flourWeight + waterWeight) { sum, (row, _) -> sum + row.weight }
        return DoughRecipe(
            portionCount = portionCount,
            portionWeightGrams = total.divide(BigDecimal(portionCount), 0, RoundingMode.FLOOR)
                .toInt(),
            hydration = hydration ?: ZERO_PERCENT,
            ingredients = usedRows.map { (_, ingredient) -> ingredient }
        )
    }

    /** What [row] puts in the recipe, or null when it is blank, 0 or can't be used. */
    private fun usedIngredient(row: ConvertedRow): Ingredient? = when {
        row.isCount -> row.toCount(portionCount)?.let {
            Ingredient(row.name.trim(), ZERO_PERCENT, it)
        }

        gramsOf(row.grams) > BigDecimal.ZERO -> usablePercentage(row.grams)?.let {
            Ingredient(row.name.trim(), it)
        }

        else -> null
    }

    /** Whether every name and amount can be used and the portions are big enough. */
    private val isComplete: Boolean
        get() = rows.namesAreValid &&
            portionWeight?.let { it >= MIN_GRAMS } == true &&
            percentageOf(water) !is Parsed.Error &&
            rows.items.none { it.amountError != null || it.gramsEachError != null }

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
            if (flourWeight == null || readGrams(water) is Parsed.Error) return null
            if (rows.items.any { it.hasUnreadableText }) return null
            val total = rows.items.fold(flourWeight + gramsOf(water)) { sum, row ->
                sum + row.weight
            }
            return total.divide(BigDecimal(portionCount), 0, RoundingMode.FLOOR).toInt()
        }

    /** The share of the flour that [text] grams make, or why it can't be; null while unknown. */
    private fun percentageOf(text: String): Parsed<Percentage>? =
        when (val grams = readGrams(text)) {
            null -> null
            is Parsed.Error -> grams
            is Parsed.Value -> usableFlourWeight?.let { percentage(grams.value, it) }
        }

    /** The share of the flour that [text] grams make, or null while blank or unusable. */
    private fun usablePercentage(text: String): Percentage? =
        (percentageOf(text) as? Parsed.Value)?.value

    /** Why this row's amount can't be used, or null when it can or is still blank. */
    private val ConvertedRow.amountError: String?
        get() = if (isCount) readCount(grams).errorMessage else percentageOf(grams).errorMessage
}

/**
 * A row for an ingredient besides flour and water, with what is typed in it: [grams] holds its
 * weight, or its amount when [isCount], so changing the choice keeps the text.
 */
data class ConvertedRow(
    val key: Int,
    val name: String,
    val grams: String,
    val isCount: Boolean = false,
    val gramsEach: String = ""
) {
    /** The name as messages and screen readers say it, standing in for a blank one. */
    val displayName: String get() = displayNameOf(name)

    /** Why [gramsEach] can't be used, or null when it can or the row isn't a count. */
    val gramsEachError: String? get() = if (isCount) gramsEachErrorOf(gramsEach) else null

    /** Whether something typed in the row isn't a number of the right form. */
    internal val hasUnreadableText: Boolean
        get() = if (isCount) {
            readCount(grams) is Parsed.Error || gramsEachError != null
        } else {
            readGrams(grams) is Parsed.Error
        }

    /** The row's weight in grams, counting what can't be used as none. */
    internal val weight: BigDecimal
        get() {
            if (!isCount) return gramsOf(grams)
            val halves = (readCount(grams) as? Parsed.Value)?.value ?: 0
            val hundredths = gramsEachHundredthsOf(gramsEach) ?: 0
            return (BigDecimal(halves) * BigDecimal(hundredths))
                .movePointLeft(GRAMS_DECIMALS)
                .divide(BigDecimal(HALVES_PER_ONE))
        }

    /** The count for a batch of [portions], or null when it is blank, 0 or can't be used. */
    internal fun toCount(portions: Int): IngredientCount? {
        val halves = (readCount(grams) as? Parsed.Value)?.value
        if (halves == null || halves == 0 || gramsEachError != null) return null
        return IngredientCount(halves, portions, gramsEachHundredthsOf(gramsEach))
    }
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

    internal fun change(key: Int, change: (ConvertedRow) -> ConvertedRow): ConvertedRows =
        copy(items = items.map { if (it.key == key) change(it) else it })
}

fun ConvertedRows.setCount(key: Int, isCount: Boolean): ConvertedRows =
    change(key) { it.copy(isCount = isCount) }

fun ConvertedRows.changeGramsEach(key: Int, text: String): ConvertedRows =
    change(key) { it.copy(gramsEach = text) }

/** Fills row [key] in from [saved]: its name and how it is measured, keeping the amount. */
fun ConvertedRows.pick(key: Int, saved: SavedIngredient): ConvertedRows = change(key) {
    it.copy(name = saved.name, isCount = saved.isCount, gramsEach = saved.gramsEachText)
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

/** The amount in halves that [text] says, or null while it is blank. */
private fun readCount(text: String): Parsed<Int>? =
    text.takeIf { it.isNotBlank() }?.let(CountRules::parse)

/** Why [text] can't be a "Grams each", or null when it can or is blank. */
internal fun gramsEachErrorOf(text: String): String? = readGrams(text).errorMessage

/** The weight of one in [text] as hundredths of a gram, or null when blank, 0 or unusable. */
internal fun gramsEachHundredthsOf(text: String): Int? =
    gramsOf(text).movePointRight(GRAMS_DECIMALS).intValueExact().takeIf { it > 0 }

/** [hundredths] of a gram as a "Grams each" shows them, without trailing zeros. */
internal fun gramsEachText(hundredths: Int): String =
    BigDecimal.valueOf(hundredths.toLong(), GRAMS_DECIMALS).stripTrailingZeros().toPlainString()

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
