package com.dice3d.model

data class RollHistoryEntry(
    val id: Long = 0,
    val timestamp: Long,
    val diceType: DiceType,
    val diceCount: Int,
    val values: List<Int>,
    val total: Int
)
