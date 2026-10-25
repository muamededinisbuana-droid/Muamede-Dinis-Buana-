package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.BuanaAiService
import com.example.data.AppDatabase
import com.example.data.ChatMessageEntity
import com.example.data.KnowledgeRepository
import com.example.data.QuickGuideItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel"

data class ChatUiState(
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val streamingText: String? = null,
    val isXimakonde: Boolean = false,
    val fontSizeScale: Float = 1.0f,
    val errorMessage: String? = null,
    val showQuickGuidesDialog: Boolean = false,
    val showEmergencyDialog: Boolean = false,
    val showBreatheDialog: Boolean = false,
    val showClearHistoryDialog: Boolean = false,
    val selectedGuide: QuickGuideItem? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val aiService = BuanaAiService()

    val messages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var lastUserPrompt: String? = null

    init {
        // If there are no messages in Room, populate the initial welcoming introduction
        viewModelScope.launch {
            val list = chatDao.getAllMessages().first()
            if (list.isEmpty()) {
                val initialText = KnowledgeRepository.getInitialGreeting(isXimakonde = false)
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "model",
                        content = initialText,
                        language = "pt",
                        category = "general"
                    )
                )
            }
        }
    }

    fun onInputTextChanged(newText: String) {
        _uiState.value = _uiState.value.copy(inputText = newText)
    }

    fun toggleLanguage() {
        val newLang = !_uiState.value.isXimakonde
        _uiState.value = _uiState.value.copy(isXimakonde = newLang)

        // Add a gentle system note in chat if desired
        viewModelScope.launch {
            val announcement = if (newLang) {
                "🇲🇿 Luga yashintjilwe ku Ximakonde. Salama! Mwamuchete chidoni? Ninkupangila chidoni lelo?"
            } else {
                "🇲🇿 Idioma alterado para Português. Como posso ajudar-te hoje com inglês, proteção da criança ou estudos?"
            }
            chatDao.insertMessage(
                ChatMessageEntity(
                    sender = "model",
                    content = announcement,
                    language = if (newLang) "xm" else "pt",
                    category = "general"
                )
            )
        }
    }

    fun setLanguage(isXimakonde: Boolean) {
        if (_uiState.value.isXimakonde != isXimakonde) {
            toggleLanguage()
        }
    }

    fun cycleFontSize() {
        val current = _uiState.value.fontSizeScale
        val next = when (current) {
            1.0f -> 1.15f
            1.15f -> 1.30f
            else -> 1.0f
        }
        _uiState.value = _uiState.value.copy(fontSizeScale = next)
    }

    fun sendMessage(content: String = _uiState.value.inputText, category: String = "general") {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || _uiState.value.isGenerating) return

        lastUserPrompt = trimmed
        _uiState.value = _uiState.value.copy(
            inputText = "",
            isGenerating = true,
            streamingText = "",
            errorMessage = null
        )

        viewModelScope.launch {
            val currentLang = if (_uiState.value.isXimakonde) "xm" else "pt"

            // 1. Save user message to Room
            chatDao.insertMessage(
                ChatMessageEntity(
                    sender = "user",
                    content = trimmed,
                    language = currentLang,
                    category = category
                )
            )

            val recentList = messages.value.takeLast(6).map { it.sender to it.content }
            val accumulated = StringBuilder()

            try {
                aiService.streamChatResponse(
                    userMessage = trimmed,
                    isXimakonde = _uiState.value.isXimakonde,
                    recentHistory = recentList
                ).collect { chunk ->
                    accumulated.append(chunk)
                    _uiState.value = _uiState.value.copy(
                        streamingText = accumulated.toString()
                    )
                }

                val finalResponse = accumulated.toString().trim()
                val responseToSave = if (finalResponse.isNotEmpty()) {
                    finalResponse
                } else {
                    getOfflineFallback(trimmed, _uiState.value.isXimakonde)
                }

                // 2. Save model reply to Room
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "model",
                        content = responseToSave,
                        language = currentLang,
                        category = category
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    streamingText = null,
                    errorMessage = null
                )

            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Buana AI request timed out", e)
                val fallback = getOfflineFallback(trimmed, _uiState.value.isXimakonde)
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "model",
                        content = "⚠️ A ligação demorou mais que o esperado. Aqui está uma orientação rápida do nosso guia:\n\n$fallback",
                        language = currentLang,
                        category = category
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    streamingText = null,
                    errorMessage = "Tempo limite excedido. Tente novamente ou use os Guias Rápidos."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Buana AI generation error", e)
                val fallback = getOfflineFallback(trimmed, _uiState.value.isXimakonde)
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "model",
                        content = "💡 Informação útil da memória offline de Buana AI:\n\n$fallback",
                        language = currentLang,
                        category = category
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    streamingText = null,
                    errorMessage = e.localizedMessage ?: "Falha na ligação à rede."
                )
            }
        }
    }

    private fun getOfflineFallback(prompt: String, isXimakonde: Boolean): String {
        val lower = prompt.lowercase()
        val guide = when {
            lower.contains("inglês") || lower.contains("english") || lower.contains("palavra") -> {
                KnowledgeRepository.quickGuides.firstOrNull { it.id == "english_basics" }
            }
            lower.contains("criança") || lower.contains("proteção") || lower.contains("abuso") || lower.contains("casamento") || lower.contains("116") -> {
                KnowledgeRepository.quickGuides.firstOrNull { it.id == "child_protection" }
            }
            lower.contains("psicossocial") || lower.contains("calma") || lower.contains("stress") || lower.contains("ansiedade") || lower.contains("triste") -> {
                KnowledgeRepository.quickGuides.firstOrNull { it.id == "psychosocial_support" }
            }
            lower.contains("estud") || lower.contains("escola") || lower.contains("exame") || lower.contains("dever") -> {
                KnowledgeRepository.quickGuides.firstOrNull { it.id == "educational_guidance" }
            }
            lower.contains("ximakonde") || lower.contains("makonde") -> {
                KnowledgeRepository.quickGuides.firstOrNull { it.id == "ximakonde_phrases" }
            }
            else -> KnowledgeRepository.quickGuides.firstOrNull { it.id == "child_protection" }
        }

        return if (guide != null) {
            if (isXimakonde) guide.offlineContentXm else guide.offlineContentPt
        } else {
            if (isXimakonde) {
                "Piga namba ya bule 116 (Linha Fala Criança) au soma vinu mu guias la Buana AI."
            } else {
                "Linha Fala Criança Gratuita: 116 | Linha Verde: 1458. Também podes consultar os nossos Guias Rápidos no menu!"
            }
        }
    }

    fun retryLastMessage() {
        val prompt = lastUserPrompt ?: return
        sendMessage(prompt)
    }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearHistory()
            val initialText = KnowledgeRepository.getInitialGreeting(_uiState.value.isXimakonde)
            chatDao.insertMessage(
                ChatMessageEntity(
                    sender = "model",
                    content = initialText,
                    language = if (_uiState.value.isXimakonde) "xm" else "pt",
                    category = "general"
                )
            )
            _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
        }
    }

    fun openQuickGuides() {
        _uiState.value = _uiState.value.copy(showQuickGuidesDialog = true)
    }

    fun closeQuickGuides() {
        _uiState.value = _uiState.value.copy(showQuickGuidesDialog = false, selectedGuide = null)
    }

    fun selectGuide(guide: QuickGuideItem) {
        _uiState.value = _uiState.value.copy(selectedGuide = guide)
    }

    fun openEmergencyDialog() {
        _uiState.value = _uiState.value.copy(showEmergencyDialog = true)
    }

    fun closeEmergencyDialog() {
        _uiState.value = _uiState.value.copy(showEmergencyDialog = false)
    }

    fun openBreatheDialog() {
        _uiState.value = _uiState.value.copy(showBreatheDialog = true)
    }

    fun closeBreatheDialog() {
        _uiState.value = _uiState.value.copy(showBreatheDialog = false)
    }

    fun showClearConfirmDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = show)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
