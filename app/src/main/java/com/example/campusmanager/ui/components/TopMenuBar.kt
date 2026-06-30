package com.example.campusmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TopMenuBar(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuAberto by remember { mutableStateOf(false) }

    val inicial = nomeUtilizador
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: "U"

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nomeUtilizador.ifBlank { "Utilizador" },
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Box {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { menuAberto = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            DropdownMenu(
                expanded = menuAberto,
                onDismissRequest = { menuAberto = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Perfil") },
                    onClick = {
                        menuAberto = false
                        onPerfilClick()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Terminar sessão") },
                    onClick = {
                        menuAberto = false
                        onLogoutClick()
                    }
                )
            }
        }
    }
}