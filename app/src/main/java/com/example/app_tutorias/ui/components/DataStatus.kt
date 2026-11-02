package com.example.app_tutorias.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.ui.UiLoadState

@Composable
fun DataStatus(
    loadState: UiLoadState,
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loadState == UiLoadState.LOADING) {
            CircularProgressIndicator()
            Text("Cargando…")
        }
        if (errorMessage != null || loadState == UiLoadState.ERROR) {
            Text(errorMessage ?: "No pudimos cargar los datos.", color = MaterialTheme.colorScheme.error)
            OutlinedButton(onClick = onRetry) { Text("Reintentar") }
        }
        if (loadState == UiLoadState.EMPTY) {
            Text("No hay datos disponibles. Si estás sin conexión, vuelve a intentarlo cuando recuperes la red.")
        }
    }
}
