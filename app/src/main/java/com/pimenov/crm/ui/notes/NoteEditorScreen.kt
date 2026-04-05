package com.pimenov.crm.ui.notes

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.FormatStrikethrough
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.pimenov.crm.core.database.model.Note
import com.pimenov.uikit.UiCoreString
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: NotesViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var title by rememberSaveable { mutableStateOf("") }
    var existingNote by rememberSaveable { mutableStateOf<Note?>(null) }
    var loaded by rememberSaveable { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    val images = remember { mutableStateListOf<String>() }

    val richTextState = rememberRichTextState()

    val speechHelper = remember {
        SpeechRecognizerHelper(
            context = context,
            onResult = { text ->
                val current = richTextState.toHtml()
                val separator = if (current.isBlank()) "" else " "
                richTextState.setHtml(current + separator + text)
            },
            onPartialResult = { },
            onListeningStateChanged = { listening -> isListening = listening },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            speechHelper.startListening()
        } else {
            Toast.makeText(
                context,
                context.getString(UiCoreString.speech_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        for (uri in uris) {
            val savedPath = copyImageToInternal(context, uri)
            if (savedPath != null) {
                images.add(savedPath)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { speechHelper.destroy() }
    }

    LaunchedEffect(noteId) {
        if (noteId != -1L && !loaded) {
            val note = viewModel.getNote(noteId)
            if (note != null) {
                title = note.title
                richTextState.setHtml(note.content)
                existingNote = note
                images.clear()
                images.addAll(note.images)
            }
            loaded = true
        } else {
            loaded = true
        }
    }

    fun performSave() {
        if (saved) return
        val htmlContent = richTextState.toHtml()
        if (title.isNotBlank() || htmlContent.isNotBlank() || images.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val note = existingNote?.copy(
                title = title,
                content = htmlContent,
                images = images.toList(),
                updatedAt = now
            ) ?: Note(
                title = title,
                content = htmlContent,
                images = images.toList(),
                updatedAt = now
            )
            viewModel.saveNote(note)
            saved = true
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
        modifier = Modifier.imePadding(),
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
                richTextState = richTextState,
                isListening = isListening,
                onMicClick = {
                    if (isListening) {
                        speechHelper.stop()
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onImageClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
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

            if (images.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images.toList()) { path ->
                        ImageThumbnail(
                            path = path,
                            onRemove = { images.remove(path) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            RichTextEditor(
                state = richTextState,
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
                colors = RichTextEditorDefaults.richTextEditorColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun ImageThumbnail(
    path: String,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.size(80.dp)) {
        AsyncImage(
            model = path,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun copyImageToInternal(context: android.content.Context, uri: Uri): String? {
    return try {
        val dir = File(context.filesDir, "note_images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun EditorToolbar(
    richTextState: com.mohamedrejeb.richeditor.model.RichTextState,
    isListening: Boolean,
    onMicClick: () -> Unit,
    onImageClick: () -> Unit,
    onSave: () -> Unit
) {
    val currentSpanStyle = richTextState.currentSpanStyle
    val isBold = currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderline = currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true
    val isStrikethrough = currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true
    val isUnorderedList = richTextState.isUnorderedList
    val isOrderedList = richTextState.isOrderedList

    Column(
        modifier = Modifier
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
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FormatButton(
                icon = Icons.Rounded.FormatBold,
                contentDescription = "Bold",
                isActive = isBold,
                onClick = {
                    richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                }
            )
            FormatButton(
                icon = Icons.Rounded.FormatItalic,
                contentDescription = "Italic",
                isActive = isItalic,
                onClick = {
                    richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                }
            )
            FormatButton(
                icon = Icons.Rounded.FormatUnderlined,
                contentDescription = "Underline",
                isActive = isUnderline,
                onClick = {
                    richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                }
            )
            FormatButton(
                icon = Icons.Rounded.FormatStrikethrough,
                contentDescription = "Strikethrough",
                isActive = isStrikethrough,
                onClick = {
                    richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                }
            )
            FormatButton(
                icon = Icons.Rounded.FormatListBulleted,
                contentDescription = "Bullet list",
                isActive = isUnorderedList,
                onClick = { richTextState.toggleUnorderedList() }
            )
            FormatButton(
                icon = Icons.Rounded.FormatListNumbered,
                contentDescription = "Numbered list",
                isActive = isOrderedList,
                onClick = { richTextState.toggleOrderedList() }
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onImageClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Image,
                    contentDescription = "Attach image",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }

            MicButton(
                isListening = isListening,
                onClick = onMicClick
            )

            IconButton(
                onClick = onSave,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun MicButton(
    isListening: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (isListening) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        label = "micTint"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .then(if (isListening) Modifier.scale(scale) else Modifier)
    ) {
        Icon(
            imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
            contentDescription = if (isListening) "Stop recording" else "Voice input",
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun FormatButton(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isActive) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}
