package com.example.keyboard.translator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val response: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _uiState.value = ChatUiState(isLoading = true, error = null)

        viewModelScope.launch {
            val result = GptClient.askModel(text.trim())

            result.onSuccess { answer ->
                _uiState.value = ChatUiState(response = answer, isLoading = false)
            }.onFailure { e ->
                _uiState.value = ChatUiState(error = e.message ?: "Ошибка сети", isLoading = false)
            }
        }
    }
}