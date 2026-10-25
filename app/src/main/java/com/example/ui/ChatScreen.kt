package com.example.ui

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BreatheExerciseDialog
import com.example.ui.components.ChatBubble
import com.example.ui.components.EmergencyContactsDialog
import com.example.ui.components.QuickGuidesDialog
import com.example.ui.components.QuickPromptChips
import com.example.ui.components.StreamingBubble
import com.example.ui.theme.BuanaAmberSecondary
import com.example.ui.theme.BuanaTealContainer
import com.example.ui.theme.BuanaTealPrimary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    var showMenu by remember { mutableStateOf(false) }

    // TTS Setup
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        var speechInstance: TextToSpeech? = null
        speechInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speechInstance?.language = Locale("pt", "MZ")
            }
        }
        tts = speechInstance
        onDispose {
            speechInstance.stop()
            speechInstance.shutdown()
        }
    }

    val onSpeak: (String) -> Unit = { textToSpeak ->
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "BuanaTTS")
    }

    // Scroll to bottom when new messages arrive or when streaming
    LaunchedEffect(messages.size, uiState.streamingText) {
        val totalCount = messages.size + (if (uiState.isGenerating) 1 else 0)
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(BuanaTealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.buana_logo),
                                contentDescription = "Buana AI Logo",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Buana AI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Active Language Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (uiState.isXimakonde) BuanaAmberSecondary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { viewModel.toggleLanguage() }
                                ) {
                                    Text(
                                        text = if (uiState.isXimakonde) "XIMAKONDE" else "PORTUGUÊS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (uiState.isXimakonde) "Kunyemelela vana • Kusoma • Chingelesi" else "Proteção da Criança • Inglês • Apoio",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Quick Language Switcher Button
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("language_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Mudar Idioma (Português / Ximakonde)",
                            tint = if (uiState.isXimakonde) BuanaAmberSecondary else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Font Size Accessibility Button
                    IconButton(
                        onClick = { viewModel.cycleFontSize() },
                        modifier = Modifier.testTag("font_size_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Tamanho do Texto",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Overflow Menu
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("overflow_menu_button")
                    ) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mais Opções")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("📘 Guias Rápidos Offline") },
                            onClick = {
                                showMenu = false
                                viewModel.openQuickGuides()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = BuanaTealPrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🛡️ Linhas de Socorro (116)") },
                            onClick = {
                                showMenu = false
                                viewModel.openEmergencyDialog()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🧘 Exercício de Respiração (4-4-4)") },
                            onClick = {
                                showMenu = false
                                viewModel.openBreatheDialog()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = BuanaTealPrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Alternar para ${if (uiState.isXimakonde) "Português" else "Ximakonde"}") },
                            onClick = {
                                showMenu = false
                                viewModel.toggleLanguage()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Translate, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Limpar Histórico") },
                            onClick = {
                                showMenu = false
                                viewModel.showClearConfirmDialog(true)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Error banner if any
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onErrorContainer)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.retryLastMessage() },
                            modifier = Modifier.size(32.dp).testTag("retry_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Tentar Novamente",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chat_messages_list")
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(
                        message = message,
                        fontSizeScale = uiState.fontSizeScale,
                        onSpeakText = onSpeak
                    )
                }

                if (uiState.isGenerating) {
                    item {
                        StreamingBubble(
                            streamingText = uiState.streamingText.orEmpty(),
                            fontSizeScale = uiState.fontSizeScale,
                            isXimakonde = uiState.isXimakonde
                        )
                    }
                }
            }

            // Quick Prompt Chips
            QuickPromptChips(
                isXimakonde = uiState.isXimakonde,
                onPromptSelected = { prompt, category ->
                    viewModel.sendMessage(prompt, category)
                },
                onOpenEmergency = { viewModel.openEmergencyDialog() },
                onOpenBreathe = { viewModel.openBreatheDialog() },
                onOpenQuickGuides = { viewModel.openQuickGuides() }
            )

            // Input Bar at the Bottom
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        placeholder = {
                            Text(
                                text = if (uiState.isXimakonde)
                                    "Lemba lilove mu Ximakonde au Português..."
                                else
                                    "Escreve em Português ou Ximakonde...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                        },
                        trailingIcon = {
                            if (uiState.inputText.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onInputTextChanged("") },
                                    modifier = Modifier.size(24.dp).testTag("clear_input_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpar texto",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (uiState.inputText.isNotBlank() && !uiState.isGenerating) {
                                    viewModel.sendMessage()
                                }
                            }
                        ),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    FloatingActionButton(
                        onClick = {
                            if (uiState.inputText.isNotBlank() && !uiState.isGenerating) {
                                viewModel.sendMessage()
                            }
                        },
                        containerColor = if (uiState.inputText.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (uiState.inputText.isNotBlank()) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("send_message_button")
                    ) {
                        if (uiState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar Mensagem",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showEmergencyDialog) {
        EmergencyContactsDialog(
            isXimakonde = uiState.isXimakonde,
            onDismiss = { viewModel.closeEmergencyDialog() }
        )
    }

    if (uiState.showBreatheDialog) {
        BreatheExerciseDialog(
            isXimakonde = uiState.isXimakonde,
            onDismiss = { viewModel.closeBreatheDialog() }
        )
    }

    if (uiState.showQuickGuidesDialog) {
        QuickGuidesDialog(
            isXimakonde = uiState.isXimakonde,
            selectedGuide = uiState.selectedGuide,
            onSelectGuide = { guide -> viewModel.selectGuide(guide ?: return@QuickGuidesDialog) },
            onAskInChat = { prompt, category ->
                viewModel.sendMessage(prompt, category)
            },
            onDismiss = { viewModel.closeQuickGuides() }
        )
    }

    if (uiState.showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearConfirmDialog(false) },
            title = {
                Text(if (uiState.isXimakonde) "Futa Vinu Vyolembwile?" else "Limpar Histórico?")
            },
            text = {
                Text(
                    if (uiState.isXimakonde)
                        "Unapenda kufuta makani ohe la Buana AI?"
                    else
                        "Tens a certeza que desejas apagar todas as mensagens da conversa?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text(if (uiState.isXimakonde) "Futa" else "Limpar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.showClearConfirmDialog(false) },
                    modifier = Modifier.testTag("cancel_clear_button")
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
