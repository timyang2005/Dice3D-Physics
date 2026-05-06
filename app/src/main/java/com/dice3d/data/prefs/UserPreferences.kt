package com.dice3d.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val KEY_DICE_TYPE = stringPreferencesKey("dice_type")
        private val KEY_DICE_COUNT = intPreferencesKey("dice_count")
        private val KEY_SHOW_TOTAL = booleanPreferencesKey("show_total")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        private val KEY_SIM_SPEED = floatPreferencesKey("sim_speed")
        private val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
    }

    val diceType: Flow<String> = context.dataStore.data.map { it[KEY_DICE_TYPE] ?: "D6" }
    val diceCount: Flow<Int> = context.dataStore.data.map { it[KEY_DICE_COUNT] ?: 1 }
    val showTotal: Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_TOTAL] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND_ENABLED] ?: true }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC_ENABLED] ?: true }
    val simulationSpeed: Flow<Float> = context.dataStore.data.map { it[KEY_SIM_SPEED] ?: 1.0f }
    val darkMode: Flow<String> = context.dataStore.data.map { it[KEY_DARK_MODE] ?: "system" }

    suspend fun saveDiceType(type: String) { context.dataStore.edit { it[KEY_DICE_TYPE] = type } }
    suspend fun saveDiceCount(count: Int) { context.dataStore.edit { it[KEY_DICE_COUNT] = count } }
    suspend fun saveShowTotal(show: Boolean) { context.dataStore.edit { it[KEY_SHOW_TOTAL] = show } }
    suspend fun saveSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled } }
    suspend fun saveHapticEnabled(enabled: Boolean) { context.dataStore.edit { it[KEY_HAPTIC_ENABLED] = enabled } }
    suspend fun saveSimulationSpeed(speed: Float) { context.dataStore.edit { it[KEY_SIM_SPEED] = speed } }
    suspend fun saveDarkMode(mode: String) { context.dataStore.edit { it[KEY_DARK_MODE] = mode } }
}
