package com.example.campusmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

data class PedidoTeste(
    val titulo: String,
    val categoria: String,
    val localizacao: String,
    val descricao: String,
    val estado: String,
    val dataCriacao: String
)

@Composable
fun MyRequestsScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var pesquisa by remember { mutableStateOf("") }
    var estadoSelecionado by remember { mutableStateOf("TODOS") }

    val pedidos = listOf(
        PedidoTeste(
            titulo = "Lâmpada avariada",
            categoria = "Manutenção",
            localizacao = "Sala A203",
            descricao = "Lâmpada fundida na sala.",
            estado = "SUBMETIDO",
            dataCriacao = "14/02/2026"
        ),
        PedidoTeste(
            titulo = "Computador sem internet",
            categoria = "Informática",
            localizacao = "Laboratório 2",
            descricao = "Computador do laboratório sem ligação à internet.",
            estado = "EM_ANALISE",
            dataCriacao = "10/02/2026"
        ),
        PedidoTeste(
            titulo = "Pedido de limpeza",
            categoria = "Limpeza",
            localizacao = "Biblioteca",
            descricao = "Zona comum precisa de limpeza.",
            estado = "CONCLUIDO",
            dataCriacao = "05/02/2026"
        ),
        PedidoTeste(
            titulo = "Porta danificada",
            categoria = "Manutenção",
            localizacao = "Bloco B",
            descricao = "Porta com problema na fechadura.",
            estado = "REJEITADO",
            dataCriacao = "01/02/2026"
        )
    )

    val estados = listOf(
        "TODOS",
        "SUBMETIDO",
        "EM_ANALISE",
        "CONCLUIDO",
        "REJEITADO"
    )

    val pedidosFiltrados = pedidos.filter { pedido ->
        val correspondeEstado =
            estadoSelecionado == "TODOS" || pedido.estado == estadoSelecionado

        val correspondePesquisa =
            pedido.titulo.contains(pesquisa, ignoreCase = true) ||
                    pedido.categoria.contains(pesquisa, ignoreCase = true) ||
                    pedido.localizacao.contains(pesquisa, ignoreCase = true) ||
                    pedido.descricao.contains(pesquisa, ignoreCase = true)

        correspondeEstado && correspondePesquisa
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FC))
    ) {
        RequestsTopBar(
            nomeUtilizador = nomeUtilizador,
            onBackClick = onBackClick,
            onPerfilClick = onPerfilClick,
            onLogoutClick = onLogoutClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = pesquisa,
                onValueChange = { pesquisa = it },
                singleLine = true,
                placeholder = {
                    Text("Pesquisar")
                },
                leadingIcon = {
                    Text("🔍", fontSize = 18.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFDDE1E7))
                    .horizontalScroll(rememberScrollState())
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                estados.forEach { estado ->
                    EstadoFilterButton(
                        estado = estado,
                        selecionado = estadoSelecionado == estado,
                        quantidade = contarPedidosPorEstado(pedidos, estado),
                        onClick = {
                            estadoSelecionado = estado
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(pedidosFiltrados) { pedido ->
                    PedidoCard(
                        pedido = pedido,
                        onDetailsClick = {
                            // Depois ligamos à página de detalhes
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestsTopBar(
    nomeUtilizador: String,
    onBackClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuAberto by remember { mutableStateOf(false) }

    val nomeMostrar = nomeUtilizador.ifBlank { "Utilizador" }.uppercase()
    val inicial = nomeUtilizador
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: "U"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "‹",
            fontSize = 38.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBackClick() }
        )

        Text(
            text = nomeMostrar,
            fontSize = 13.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

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
private fun EstadoFilterButton(
    estado: String,
    selecionado: Boolean,
    quantidade: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selecionado) Color.White else Color.Transparent,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier.height(38.dp)
    ) {
        Text(
            text = "${formatarEstado(estado)}($quantidade)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PedidoCard(
    pedido: PedidoTeste,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = pedido.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF73879A)
                    )

                    Text(
                        text = pedido.categoria,
                        fontSize = 13.sp,
                        color = Color(0xFF73879A)
                    )
                }

                EstadoBadge(estado = pedido.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📅 Criado em ${pedido.dataCriacao}",
                fontSize = 10.sp,
                color = Color.Black
            )

            Text(
                text = "📍 ${pedido.localizacao}",
                fontSize = 10.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDetailsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7A7A7A),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "Ver detalhes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EstadoBadge(
    estado: String
) {
    val backgroundColor = when (estado) {
        "SUBMETIDO" -> Color(0xFFFFF8C5)
        "EM_ANALISE" -> Color(0xFFD9EAFE)
        "CONCLUIDO" -> Color(0xFFD7FFD9)
        "REJEITADO" -> Color(0xFFFFD6D6)
        else -> Color(0xFFE5E7EB)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = formatarEstado(estado),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

private fun contarPedidosPorEstado(
    pedidos: List<PedidoTeste>,
    estado: String
): Int {
    return if (estado == "TODOS") {
        pedidos.size
    } else {
        pedidos.count { it.estado == estado }
    }
}

fun formatarEstado(estado: String): String {
    return when (estado) {
        "TODOS" -> "Todos"
        "SUBMETIDO" -> "Submetido"
        "EM_ANALISE" -> "Em análise"
        "CONCLUIDO" -> "Concluído"
        "REJEITADO" -> "Rejeitado"
        else -> estado
    }
}