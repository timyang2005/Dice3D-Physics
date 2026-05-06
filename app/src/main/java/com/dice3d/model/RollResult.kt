package com.dice3d.model

data class RollResult(
    val diceType: DiceType,
    val value: Int,
    val timestamp: Long = System.currentTimeMillis()
)
