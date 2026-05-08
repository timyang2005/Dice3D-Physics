package com.dice3d.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roll_history")
data class RollHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val diceTypeName: String,
    val diceCount: Int,
    val values: String,
    val total: Int
)
