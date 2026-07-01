package com.example.campusmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.sp

@Composable
fun AdminHomeScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onRequestsByStatusClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FC))
    ) {
        AdminTopBar(
            nomeUtilizador = nomeUtilizador,
            onPerfilClick = onPerfilClick,
            onLogoutClick = onLogoutClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminDashboardCard(
                    title = "Submetidos",
                    value = "4",
                    modifier = Modifier.weight(1f)
                )

                AdminDashboardCard(
                    title = "Em análise",
                    value = "2",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminDashboardCard(
                    title = "Concluídos",
                    value = "3",
                    modifier = Modifier.weight(1f)
                )

                AdminDashboardCard(
                    title = "Rejeitados",
                    value = "1",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            AdminDashboardButton(
                text = "Todos os pedidos",
                onClick = onRequestsByStatusClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminDashboardButton(
                text = "Pedidos por estado",
                onClick = onRequestsByStatusClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminDashboardButton(
                text = "Gerir categorias",
                onClick = onCategoriesClick
            )
        }
    }
}

@Composable
private fun AdminTopBar(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuAberto by remember { mutableStateOf(false) }

    val nomeMostrar = nomeUtilizador.ifBlank { "Administrador" }.uppercase()
    val inicial = nomeUtilizador
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: "A"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "🔔",
            fontSize = 22.sp,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nomeMostrar,
                fontSize = 13.sp,
                color = Color.Black
            )

            Text(
                text = "ADMINISTRADOR",
                fontSize = 10.sp,
                color = Color(0xFF6B7C93)
            )
        }

        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8D7FF))
                    .clickable { menuAberto = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B21B6)
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

@Composable
private fun AdminDashboardCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(124.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF6B7C93)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun AdminDashboardButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDDE1E7),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}