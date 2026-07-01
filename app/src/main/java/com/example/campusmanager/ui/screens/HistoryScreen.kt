package com.example.campusmanager.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun HistoryScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    RequestsListBaseScreen(
        nomeUtilizador = nomeUtilizador,
        tituloTopBar = "HISTÓRICO",
        estadosPermitidos = listOf(
            "CONCLUIDO",
            "REJEITADO"
        ),
        mensagemVazia = "Não existem pedidos no histórico.",
        permitirCancelamento = false,
        onPerfilClick = onPerfilClick,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick
    )
}