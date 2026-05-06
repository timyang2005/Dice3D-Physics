package com.dice3d.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dice3d.data.prefs.UserPreferences
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

    fun setDarkMode(mode: String) { viewModelScope.launch { prefs.saveDarkMode(mode) } }
    fun setShowTotal(show: Boolean) { viewModelScope.launch { prefs.saveShowTotal(show) } }
    fun setSoundEnabled(enabled: Boolean) { viewModelScope.launch { prefs.saveSoundEnabled(enabled) } }
    fun setHapticEnabled(enabled: Boolean) { viewModelScope.launch { prefs.saveHapticEnabled(enabled) } }
}
