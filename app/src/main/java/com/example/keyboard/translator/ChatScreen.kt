package com.example.keyboard.translator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(" YandexGPT Lite", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Введите запрос...") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            singleLine = false
        )

        Button(
            onClick = {
                viewModel.sendMessage(inputText)
                inputText = ""
            },
            enabled = !uiState.isLoading && inputText.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Отправить")
            }
        }

        uiState.error?.let { err ->
            Text("❌ $err", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        uiState.response?.let { resp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = resp,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

    }
}