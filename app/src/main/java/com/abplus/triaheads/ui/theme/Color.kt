package com.abplus.triaheads.ui.theme

import androidx.compose.ui.graphics.Color
val Blue10 = Color(0xFF82B1FF)
val Blue40 = Color(0xFF2196F3)
val BlueGrey40 = Color(0xFF42A5F5)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val White50 = Color(0x80FFFFFF)

val FabColor = Color(0xFFFF6666)

fun agedBlack(createdAt: Long): Color  {
    val age = (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60 * 24)
    return when {
        age < 1 -> Black
        age < 2 -> Black.copy(alpha = 0.8f)
        age < 7 -> Black.copy(alpha = 0.5f)
        age < 30 -> Black.copy(alpha = 0.3f)
        else -> Black.copy(alpha = 0.2f)
    }
}
