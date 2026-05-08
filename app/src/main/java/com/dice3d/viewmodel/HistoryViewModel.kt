package com.dice3d.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dice3d.data.db.AppDatabase
import com.dice3d.data.db.RollHistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).rollHistoryDao()
    val historyList: StateFlow<List<RollHistoryEntity>> = dao.getAllHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteEntry(entry: RollHistoryEntity) { viewModelScope.launch { dao.delete(entry) } }
    fun clearAll() { viewModelScope.launch { dao.deleteAll() } }
    fun getStatsForType(typeName: String): StateFlow<List<RollHistoryEntity>> = dao.getHistoryByType(typeName).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
