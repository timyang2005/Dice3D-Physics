package com.dice3d.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import com.dice3d.model.computeContrastNumberColor
import androidx.lifecycle.viewModelScope
import com.dice3d.audio.SoundManager
import com.dice3d.data.db.AppDatabase
import com.dice3d.data.db.RollHistoryEntity
import com.dice3d.haptic.HapticManager
import com.dice3d.model.DiceConfig
import com.dice3d.model.DiceType
import com.dice3d.physics.DiceRigidBody
import com.dice3d.physics.DiceShapeFactory
import com.dice3d.physics.PhysicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.vecmath.Vector3f

data class DiceUiState(
    val diceConfig: DiceConfig = DiceConfig(),
    val isRolling: Boolean = false,
    val results: List<Int> = emptyList(),
    val total: Int = 0,
    val showTotal: Boolean = true,
    val simulationSpeed: Float = 1.0f,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val isDarkMode: Boolean? = null
)

class DiceViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(DiceUiState())
    val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()
    val physicsWorld: PhysicsWorld
    private val diceBodies = mutableListOf<DiceRigidBody>()
    private var physicsJob: Job? = null
    private var soundManager: SoundManager? = null
    private var hapticManager: HapticManager? = null
    private var historyDaoInitialized = false
    private var historyDao: com.dice3d.data.db.RollHistoryDao? = null

    init {
        physicsWorld = try { PhysicsWorld() } catch (_: Exception) { PhysicsWorld() }
        try { soundManager = SoundManager(application) } catch (_: Exception) {}
        try { hapticManager = HapticManager(application) } catch (_: Exception) {}
        try { historyDao = AppDatabase.getInstance(application).rollHistoryDao(); historyDaoInitialized = true } catch (_: Exception) {}
        startPhysicsLoop()
    }

    fun rollDice() {
        try {
            if (diceBodies.isEmpty()) spawnDice()
            _uiState.value = _uiState.value.copy(isRolling = true, results = emptyList(), total = 0)
            physicsWorld.applyRandomImpulseToAll()
            if (_uiState.value.soundEnabled) try { soundManager?.playRoll() } catch (_: Exception) {}
            if (_uiState.value.hapticEnabled) try { hapticManager?.onRoll() } catch (_: Exception) {}
        } catch (_: Exception) {}
    }

    fun updateDiceConfig(config: DiceConfig) {
        _uiState.value = _uiState.value.copy(diceConfig = config)
        clearDice(); spawnDice()
    }

    fun setDiceCount(count: Int) { updateDiceConfig(_uiState.value.diceConfig.copy(count = count.coerceIn(1, 6))) }
    fun setDiceColor(color: Color) { updateDiceConfig(_uiState.value.diceConfig.copy(bodyColor = color, numberColor = color.computeContrastNumberColor())) }
    fun setShowTotal(show: Boolean) { _uiState.value = _uiState.value.copy(showTotal = show) }
    fun setSimulationSpeed(speed: Float) { _uiState.value = _uiState.value.copy(simulationSpeed = speed.coerceIn(0.1f, 5.0f)) }
    fun setSoundEnabled(enabled: Boolean) { _uiState.value = _uiState.value.copy(soundEnabled = enabled); try { soundManager?.enabled = enabled } catch (_: Exception) {} }
    fun setHapticEnabled(enabled: Boolean) { _uiState.value = _uiState.value.copy(hapticEnabled = enabled); try { hapticManager?.enabled = enabled } catch (_: Exception) {} }
    fun setDarkMode(darkMode: Boolean?) { _uiState.value = _uiState.value.copy(isDarkMode = darkMode) }
    fun applyGravitySensor(dx: Float, dy: Float, dz: Float) { try { physicsWorld.applyGravitySensor(dx, dy, dz) } catch (_: Exception) {} }

    private fun spawnDice() {
        try {
            val config = _uiState.value.diceConfig
            val shape = DiceShapeFactory.createShape(DiceType.D6)
            for (i in 0 until config.count) {
                val mass = 1f; val localInertia = Vector3f(0f,0f,0f)
                shape.calculateLocalInertia(mass, localInertia)
                val startTransform = Transform().apply { setIdentity(); origin.set(Vector3f((i-config.count/2f)*2.5f, 5f+i*2f, 0f)) }
                val motionState = DefaultMotionState(startTransform)
                val rbInfo = RigidBodyConstructionInfo(mass, motionState, shape, localInertia)
                val body = RigidBody(rbInfo)
                body.restitution = 0.3f; body.friction = 0.8f
                body.forceActivationState(RigidBody.WANTS_DEACTIVATION)
                val diceRigidBody = DiceRigidBody(DiceType.D6, body)
                physicsWorld.addDice(diceRigidBody); diceBodies.add(diceRigidBody)
            }
        } catch (_: Exception) {}
    }

    private fun clearDice() { try { physicsWorld.clearAllDice(); diceBodies.clear() } catch (_: Exception) {} }

    private fun saveRollResult(results: List<Int>) {
        if (!historyDaoInitialized) return
        val config = _uiState.value.diceConfig
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyDao?.insert(RollHistoryEntity(
                    timestamp = System.currentTimeMillis(),
                    diceTypeName = "D6",
                    diceCount = config.count,
                    values = results.joinToString(", "),
                    total = results.sum()
                ))
            } catch (_: Exception) {}
        }
    }

    private fun startPhysicsLoop() {
        physicsJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                try {
                    val speed = _uiState.value.simulationSpeed
                    physicsWorld.stepSimulation(1f/60f, speed)
                    if (_uiState.value.isRolling && physicsWorld.areAllDiceSettled()) {
                        val results = physicsWorld.getDiceResults()
                        _uiState.value = _uiState.value.copy(isRolling = false, results = results, total = results.sum())
                        try { if (_uiState.value.hapticEnabled) hapticManager?.onDiceHit(0.5f) } catch (_: Exception) {}
                        try { if (_uiState.value.soundEnabled) soundManager?.playHit(0.5f) } catch (_: Exception) {}
                        saveRollResult(results)
                    }
                } catch (_: Exception) {}
                delay(16)
            }
        }
    }

    override fun onCleared() { super.onCleared(); physicsJob?.cancel(); try { physicsWorld.destroy() } catch (_: Exception) {}; try { soundManager?.release() } catch (_: Exception) {} }
}
