package com.example.campusmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    nomeUtilizador: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Perfil")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nome: $nomeUtilizador")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBackClick) {
            Text("Voltar")
        }
    }
}