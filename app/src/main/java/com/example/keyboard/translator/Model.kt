package com.example.keyboard.translator

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslatorViewModel(application: Application) : AndroidViewModel(application) {

    private val translator: DictionaryTranslator by lazy {
        DictionaryTranslator(getApplication())
    }

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun translate(input: String) {
        Log.d("TRACE", "⚙️ ViewModel.translate() вызван с: '$input'")
        if (input.isBlank()) {
            _translatedText.value = ""
            _errorMessage.value = null
            return
        }

        viewModelScope.launch {
            Log.d("TRACE", "🔍 Запрос к БД...")
            _isLoading.value = true
            _errorMessage.value = null
            try {

                val words = input.trim().split("\\s+".toRegex())
                val translated = words.map { word ->
                    translator.translate(word) ?: word // Если не найдено → оставляем оригинал

                }
                _translatedText.value = translated.joinToString(" ")
                // В TranslatorViewModel.kt, внутри fun translate(input: String)
                val result = translated.joinToString(" ")
                Log.d("VM_FLOW", "📤 Отправляю в UI: '$result'")
                _translatedText.value = result
            } catch (e: Exception) {
                _errorMessage.value = "⚠️ Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }

    }

    override fun onCleared() {
        try { translator.close() } catch (_: Exception) {}
        super.onCleared()
    }
}