package com.example.app_tutorias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inicio",
            style = MaterialTheme.typography.displaySmall
        )

        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir a Buscar")
        }

        Button(
            onClick = onCalendarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir a Calendario")
        }

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir a Configuración")
        }
    }
}