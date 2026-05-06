package com.dice3d.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RollHistoryDao {
    @Query("SELECT * FROM roll_history ORDER BY timestamp DESC") fun getAllHistory(): Flow<List<RollHistoryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(entry: RollHistoryEntity): Long
    @Delete suspend fun delete(entry: RollHistoryEntity)
    @Query("DELETE FROM roll_history") suspend fun deleteAll()
    @Query("SELECT * FROM roll_history WHERE diceTypeName = :typeName ORDER BY timestamp DESC") fun getHistoryByType(typeName: String): Flow<List<RollHistoryEntity>>
}
