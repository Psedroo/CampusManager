package com.example.campusmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campusmanager.ui.components.TopMenuBar

@Composable
fun AdminHomeScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onRequestsByStatusClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopMenuBar(
            nomeUtilizador = nomeUtilizador,
            onPerfilClick = onPerfilClick,
            onLogoutClick = onLogoutClick
        )

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Menu do Administrador")

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onRequestsByStatusClick) {
                Text("Todos os Pedidos")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onRequestsByStatusClick) {
                Text("Pedidos por Estado")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onCategoriesClick) {
                Text("Gerir Categorias")
            }
        }
    }
}