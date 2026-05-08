package com.dice3d.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice3d.renderer.DiceGLSurfaceView
import com.dice3d.renderer.DiceRenderer
import com.dice3d.ui.theme.MorandiRose
import com.dice3d.ui.theme.MorandiSage
import com.dice3d.ui.theme.MorandiBlue
import com.dice3d.ui.theme.MorandiSand
import com.dice3d.ui.theme.MorandiSlate
import com.dice3d.ui.theme.MorandiCream
import com.dice3d.viewmodel.DiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(viewModel: DiceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfigSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showConfigSheet) {
        ModalBottomSheet(
            onDismissRequest = { showConfigSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 32.dp)) {
                Text("骰子数量: ${uiState.diceConfig.count}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(value = uiState.diceConfig.count.toFloat(), onValueChange = { viewModel.setDiceCount(it.toInt()) }, valueRange = 1f..6f, steps = 4, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary))

                Spacer(modifier = Modifier.height(16.dp))
                Text("骰子颜色", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colors = listOf(MorandiRose, MorandiSage, MorandiBlue, MorandiSand, MorandiSlate, MorandiCream, Color(0xFF4A4238))
                    colors.forEach { color ->
                        val selected = uiState.diceConfig.bodyColor == color
                        Box(modifier = Modifier.size(36.dp).clickable { viewModel.setDiceColor(color) }.background(color, CircleShape).border(
                            if (selected) 3.dp else 1.dp,
                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            CircleShape
                        ))
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                DiceGLSurfaceView(ctx).apply {
                    val r = DiceRenderer(ctx, viewModel.physicsWorld, getCameraController())
                    setRenderer(r)
                    onRollRequested = { viewModel.rollDice() }
                }
            },
            update = { view ->
                view.renderer?.let { r ->
                    r.isDarkMode = uiState.isDarkMode == true
                    r.diceConfig = uiState.diceConfig
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

            Text("D6 × ${uiState.diceConfig.count}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(4.dp))

            Text("速度: ${String.format("%.1f", uiState.simulationSpeed)}×", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = uiState.simulationSpeed,
                onValueChange = { viewModel.setSimulationSpeed(it) },
                valueRange = 0.1f..5.0f,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                FloatingActionButton(
                    onClick = { showConfigSheet = true },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Text("⚙", fontSize = 22.sp)
                }

                FloatingActionButton(
                    onClick = { viewModel.rollDice() },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Text("🎲", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(56.dp))
            }
        }
    }
}
