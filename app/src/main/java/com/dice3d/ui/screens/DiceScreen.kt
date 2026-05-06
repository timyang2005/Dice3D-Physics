package com.dice3d.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice3d.renderer.DiceGLSurfaceView
import com.dice3d.renderer.DiceRenderer
import com.dice3d.viewmodel.DiceViewModel

@Composable
fun DiceScreen(viewModel: DiceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                DiceGLSurfaceView(ctx).apply {
                    val renderer = DiceRenderer(ctx, viewModel.physicsWorld, getCameraController())
                    setRenderer(renderer)
                    onRollRequested = { viewModel.rollDice() }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.results.isNotEmpty()) {
                Text(
                    text = if (uiState.results.size > 1 && uiState.showTotal) "结果: ${uiState.results.joinToString(", ")}  总和: ${uiState.total}" else "结果: ${uiState.results.joinToString(", ")}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text("速度: ${String.format("%.1f", uiState.simulationSpeed)}×", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = uiState.simulationSpeed,
                onValueChange = { viewModel.setSimulationSpeed(it) },
                valueRange = 0.1f..5.0f,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FloatingActionButton(
                onClick = { viewModel.rollDice() },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Text(if (uiState.isRolling) "🔀" else "🎲", fontSize = 28.sp)
            }
        }
    }
}
