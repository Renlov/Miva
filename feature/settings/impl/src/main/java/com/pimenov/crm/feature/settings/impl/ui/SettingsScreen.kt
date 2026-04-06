package com.pimenov.crm.feature.settings.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pimenov.uikit.UiCoreString
import com.pimenov.crm.feature.settings.impl.data.AppLanguage
import com.pimenov.crm.feature.settings.impl.data.SettingsState
import com.pimenov.crm.feature.settings.impl.data.ThemeMode
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val settings by viewModel.settings.collectAsState()
    SettingsContent(
        settings = settings,
        onThemeChange = viewModel::setThemeMode,
        onLanguageChange = viewModel::setLanguage,
        onApiKeySave = viewModel::setApiKey,
        onAiDailyLimitChange = viewModel::setAiDailyLimit,
        onAiModelChange = viewModel::setAiModel,
        onNotificationsToggle = viewModel::setNotificationsEnabled,
        onNotifyTaskDueToggle = viewModel::setNotifyOnTaskDue,
        onNotifyAiReplyToggle = viewModel::setNotifyOnAiReply
    )
}

@Composable
private fun SettingsContent(
    settings: SettingsState,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onApiKeySave: (String) -> Unit,
    onAiDailyLimitChange: (Int) -> Unit,
    onAiModelChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onNotifyTaskDueToggle: (Boolean) -> Unit,
    onNotifyAiReplyToggle: (Boolean) -> Unit
) {
    var apiKeyInput by rememberSaveable { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(UiCoreString.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            // Theme
            SectionTitle(stringResource(UiCoreString.settings_section_appearance))
            SettingsCard {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.themeMode == mode,
                            onClick = { onThemeChange(mode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> stringResource(UiCoreString.settings_theme_light)
                                ThemeMode.DARK -> stringResource(UiCoreString.settings_theme_dark)
                                ThemeMode.SYSTEM -> stringResource(UiCoreString.settings_theme_system)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Language
            SectionTitle(stringResource(UiCoreString.settings_section_language))
            SettingsCard {
                AppLanguage.entries.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.language == lang,
                            onClick = { onLanguageChange(lang) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = when (lang) {
                                AppLanguage.SYSTEM -> stringResource(UiCoreString.settings_language_system)
                                AppLanguage.RU -> stringResource(UiCoreString.settings_language_ru)
                                AppLanguage.EN -> stringResource(UiCoreString.settings_language_en)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // AI Settings
            SectionTitle(stringResource(UiCoreString.settings_section_ai))
            SettingsCard {
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(UiCoreString.settings_ai_api_key_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { onApiKeySave(apiKeyInput.trim()) },
                    enabled = apiKeyInput.isNotBlank()
                ) {
                    Text(stringResource(UiCoreString.settings_ai_save_key))
                }

                Spacer(Modifier.height(16.dp))

                // AI Model
                AiModelDropdown(
                    selectedModel = settings.aiModel,
                    onModelChange = onAiModelChange
                )

                Spacer(Modifier.height(16.dp))

                // Daily limit
                Text(
                    text = stringResource(UiCoreString.settings_ai_daily_limit, settings.aiDailyLimit),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = settings.aiDailyLimit.toFloat(),
                    onValueChange = { onAiDailyLimitChange(it.roundToInt()) },
                    valueRange = 5f..200f,
                    steps = 38,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            // Notifications
            SectionTitle(stringResource(UiCoreString.settings_section_notifications))
            SettingsCard {
                SwitchRow(
                    label = stringResource(UiCoreString.settings_notifications_enabled),
                    checked = settings.notificationsEnabled,
                    onCheckedChange = onNotificationsToggle
                )
                SwitchRow(
                    label = stringResource(UiCoreString.settings_notifications_task_due),
                    checked = settings.notifyOnTaskDue,
                    enabled = settings.notificationsEnabled,
                    onCheckedChange = onNotifyTaskDueToggle
                )
                SwitchRow(
                    label = stringResource(UiCoreString.settings_notifications_ai_reply),
                    checked = settings.notifyOnAiReply,
                    enabled = settings.notificationsEnabled,
                    onCheckedChange = onNotifyAiReplyToggle
                )
            }

            Spacer(Modifier.height(20.dp))

            // About
            SectionTitle(stringResource(UiCoreString.settings_section_about))
            SettingsCard {
                Text(
                    text = stringResource(UiCoreString.settings_about_version),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiModelDropdown(selectedModel: String, onModelChange: (String) -> Unit) {
    val models = listOf("gpt-4o-mini", "gpt-4o", "gpt-4-turbo", "gpt-3.5-turbo")
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedModel,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(UiCoreString.settings_ai_model)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = {
                        onModelChange(model)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
