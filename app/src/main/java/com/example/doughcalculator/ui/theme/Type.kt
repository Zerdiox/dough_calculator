package com.example.doughcalculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.doughcalculator.R

private val YoungSerif = FontFamily(Font(R.font.young_serif))

private val Default = Typography()

/** Young Serif for screen titles, card headings and the big numbers; the system font elsewhere. */
val Typography = Typography(
    displaySmall = Default.displaySmall.copy(
        fontFamily = YoungSerif,
        fontSize = 44.sp,
        lineHeight = 52.sp
    ),
    headlineMedium = Default.headlineMedium.copy(
        fontFamily = YoungSerif,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = Default.headlineSmall.copy(
        fontFamily = YoungSerif,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = Default.titleLarge.copy(
        fontFamily = YoungSerif,
        fontSize = 24.sp,
        lineHeight = 30.sp
    )
)
