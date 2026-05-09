package com.dice3d.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dice3d.DiceApp
import com.dice3d.data.prefs.UserPreferences
import com.dice3d.logging.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UserPreferences(application)
    val darkMode: StateFlow<String> = prefs.darkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val showTotal: StateFlow<Boolean> = prefs.showTotal.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val soundEnabled: StateFlow<Boolean> = prefs.soundEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val hapticEnabled: StateFlow<Boolean> = prefs.hapticEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val logExportStatus = MutableStateFlow<LogExportStatus>(LogExportStatus.Idle)

    sealed class LogExportStatus {
        object Idle : LogExportStatus()
        object Exporting : LogExportStatus()
        data class Success(val filePath: String) : LogExportStatus()
        data class Error(val message: String) : LogExportStatus()
    }

    fun setDarkMode(mode: String) { viewModelScope.launch { prefs.saveDarkMode(mode) } }
    fun setShowTotal(show: Boolean) { viewModelScope.launch { prefs.saveShowTotal(show) } }
    fun setSoundEnabled(enabled: Boolean) { viewModelScope.launch { prefs.saveSoundEnabled(enabled) } }
    fun setHapticEnabled(enabled: Boolean) { viewModelScope.launch { prefs.saveHapticEnabled(enabled) } }

    fun exportLog() {
        viewModelScope.launch {
            logExportStatus.value = LogExportStatus.Exporting
            try {
                val app = getApplication<DiceApp>()
                val file = app.crashLogManager.exportLog()
                if (file != null) {
                    AppLogger.i(TAG, "Log exported to ${file.absolutePath}")
                    logExportStatus.value = LogExportStatus.Success(file.absolutePath)
                } else {
                    logExportStatus.value = LogExportStatus.Error("Failed to export log")
                }
            } catch (e: Exception) {
                AppLogger.e(TAG, "Export log error: ${e.message}")
                logExportStatus.value = LogExportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
