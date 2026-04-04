package com.pimenov.crm.ui.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pimenov.crm.core.database.model.Note
import com.pimenov.uikit.UiCoreString
import org.koin.androidx.compose.koinViewModel

private data class EditorSnapshot(
    val title: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: NotesViewModel = koinViewModel()
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var existingNote by rememberSaveable { mutableStateOf<Note?>(null) }
    var loaded by rememberSaveable { mutableStateOf(false) }

    // Undo/Redo history
    val history = remember { mutableStateListOf<EditorSnapshot>() }
    var historyIndex by remember { mutableIntStateOf(-1) }
    var isRestoringSnapshot by remember { mutableStateOf(false) }

    fun pushSnapshot(t: String, c: String) {
        if (isRestoringSnapshot) return
        val current = if (historyIndex >= 0 && historyIndex < history.size) history[historyIndex] else null
        if (current != null && current.title == t && current.content == c) return

        // Drop any redo states after current index
        while (history.size > historyIndex + 1) {
            history.removeAt(history.lastIndex)
        }
        history.add(EditorSnapshot(t, c))
        historyIndex = history.lastIndex
    }

    val canUndo = historyIndex > 0
    val canRedo = historyIndex < history.lastIndex

    LaunchedEffect(noteId) {
        if (noteId != -1L && !loaded) {
            val note = viewModel.getNote(noteId)
            if (note != null) {
                title = note.title
                content = note.content
                existingNote = note
            }
            loaded = true
        } else {
            loaded = true
        }
        pushSnapshot(title, content)
    }

    // Snapshot on word boundaries: space/newline/punctuation triggers immediate save,
    // otherwise debounce 500ms so each undo step ≈ one word
    LaunchedEffect(title, content) {
        if (!loaded || isRestoringSnapshot) return@LaunchedEffect

        val prev = if (historyIndex >= 0 && historyIndex < history.size) history[historyIndex] else null
        val titleChanged = prev == null || prev.title != title
        val contentChanged = prev == null || prev.content != content

        val changedText = when {
            contentChanged -> content
            titleChanged -> title
            else -> return@LaunchedEffect
        }

        // If last char is a word boundary, snapshot immediately
        val lastChar = changedText.lastOrNull()
        if (lastChar != null && (lastChar == ' ' || lastChar == '\n' || lastChar in ".,;:!?-—()")) {
            pushSnapshot(title, content)
        } else {
            // Debounce — snapshot after 500ms pause
            delay(500)
            pushSnapshot(title, content)
        }
    }

    fun performSave() {
        if (title.isNotBlank() || content.isNotBlank()) {
            val note = existingNote?.copy(
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis()
            ) ?: Note(title = title, content = content)
            viewModel.saveNote(note)
        }
    }

    DisposableEffect(Unit) {
        onDispose { performSave() }
    }

    val transparent = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (noteId == -1L) UiCoreString.notes_editor_new_title
                            else UiCoreString.notes_editor_edit_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(UiCoreString.notes_editor_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            EditorToolbar(
                canUndo = canUndo,
                canRedo = canRedo,
                onUndo = {
                    if (canUndo) {
                        isRestoringSnapshot = true
                        historyIndex--
                        val snapshot = history[historyIndex]
                        title = snapshot.title
                        content = snapshot.content
                        isRestoringSnapshot = false
                    }
                },
                onRedo = {
                    if (canRedo) {
                        isRestoringSnapshot = true
                        historyIndex++
                        val snapshot = history[historyIndex]
                        title = snapshot.title
                        content = snapshot.content
                        isRestoringSnapshot = false
                    }
                },
                onSave = {
                    performSave()
                    onBack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(UiCoreString.notes_editor_title_hint),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = transparent,
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))

            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = {
                    Text(
                        stringResource(UiCoreString.notes_editor_content_hint),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = transparent
            )
        }
    }
}

@Composable
private fun EditorToolbar(
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Undo,
                    contentDescription = "Отменить",
                    tint = if (canUndo) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Redo,
                    contentDescription = "Повторить",
                    tint = if (canRedo) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onSave,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Сохранить",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
