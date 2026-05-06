package com.dice3d.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dice3d.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val darkMode by viewModel.darkMode.collectAsState()
    val showTotal by viewModel.showTotal.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("设置") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text("外观", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top=16.dp, bottom=8.dp))
            listOf("system" to "跟随系统", "light" to "浅色模式", "dark" to "深色模式").forEach { (value, label) ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = darkMode==value, onClick = { viewModel.setDarkMode(value) })
                    Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start=8.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical=12.dp))
            Text("显示", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom=8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text("显示总和"); Text("在投掷结果中显示点数总和", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = showTotal, onCheckedChange = { viewModel.setShowTotal(it) })
            }
            HorizontalDivider(modifier = Modifier.padding(vertical=12.dp))
            Text("反馈", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom=8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text("音效"); Text("启用投掷与碰撞音效", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = soundEnabled, onCheckedChange = { viewModel.setSoundEnabled(it) })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text("触觉反馈"); Text("碰撞时触发震动", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = hapticEnabled, onCheckedChange = { viewModel.setHapticEnabled(it) })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
