package com.dice3d.model

import androidx.compose.ui.graphics.Color

fun Color.computeContrastNumberColor(): Color {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luminance > 0.5f) Color(0xFF1A1A1A) else Color.White
}

data class DiceConfig(
    val diceType: DiceType = DiceType.D6,
    val count: Int = 1,
    val bodyColor: Color = Color.White,
    val numberColor: Color = Color(0xFF1A1A1A),
    val scale: Float = 1.0f
)
