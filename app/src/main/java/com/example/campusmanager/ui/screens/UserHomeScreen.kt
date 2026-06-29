package com.example.campusmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserHomeScreen(
    onCreateRequestClick: () -> Unit,
    onMyRequestsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menu do Utilizador")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onCreateRequestClick) {
            Text("Criar Pedido")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onMyRequestsClick) {
            Text("Os Meus Pedidos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { }) {
            Text("Histórico")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onLogoutClick) {
            Text("Terminar Sessão")
        }
    }
}