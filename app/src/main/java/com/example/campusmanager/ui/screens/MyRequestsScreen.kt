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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.campusmanager.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

data class Pedido(
    val id: Long,
    val categoriaId: Long?,
    val categoria: String,
    val localizacao: String,
    val descricao: String,
    val estado: String,
    val dataCriacao: String,
    val fotoUrl: String?
)

@Composable
fun MyRequestsScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    RequestsListBaseScreen(
        nomeUtilizador = nomeUtilizador,
        tituloTopBar = "OS MEUS PEDIDOS",
        estadosPermitidos = listOf(
            "SUBMETIDO",
            "EM_ANALISE"
        ),
        mensagemVazia = "Não existem pedidos ativos para mostrar.",
        permitirCancelamento = true,
        onPerfilClick = onPerfilClick,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick

    )

}

@Composable
fun RequestsListBaseScreen(
    nomeUtilizador: String,
    tituloTopBar: String,
    estadosPermitidos: List<String>,
    mensagemVazia: String,
    permitirCancelamento: Boolean,
    onPerfilClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var pesquisa by remember { mutableStateOf("") }
    var estadoSelecionado by remember { mutableStateOf("TODOS") }

    var pedidoSelecionado by remember { mutableStateOf<Pedido?>(null) }
    var pedidoParaEliminar by remember { mutableStateOf<Pedido?>(null) }
    var pedidoParaEditar by remember { mutableStateOf<Pedido?>(null) }
    var isEditarLoading by remember { mutableStateOf(false) }
    var fotoAbertaUrl by remember { mutableStateOf<String?>(null) }

    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var isLoadingPedidos by remember { mutableStateOf(true) }
    var isEliminarLoading by remember { mutableStateOf(false) }
    var erroCarregamento by remember { mutableStateOf("") }
    var reloadTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    val estados = listOf("TODOS") + estadosPermitidos

    LaunchedEffect(reloadTrigger) {
        try {
            isLoadingPedidos = true
            erroCarregamento = ""

            val user = SupabaseClient.client.auth.currentUserOrNull()

            if (user == null) {
                erroCarregamento = "Sessão inválida. Inicia sessão novamente."
                isLoadingPedidos = false
                return@LaunchedEffect
            }

            val categoriasResultado = SupabaseClient.client
                .from("categories")
                .select()

            val categoriasMap = categoriasResultado
                .decodeList<JsonObject>()
                .associate { categoria ->
                    val id = categoria["id"]?.jsonPrimitive?.longOrNull ?: 0L
                    val nome = categoria["nome"]?.jsonPrimitive?.contentOrNull ?: "Sem categoria"

                    id to nome
                }

            val pedidosResultado = SupabaseClient.client
                .from("requests")
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }

            pedidos = pedidosResultado
                .decodeList<JsonObject>()
                .map { pedido ->
                    val id = pedido["id"]?.jsonPrimitive?.longOrNull ?: 0L
                    val categoriaId = pedido["category_id"]?.jsonPrimitive?.longOrNull
                    val createdAt = pedido["created_at"]?.jsonPrimitive?.contentOrNull ?: "Sem data"

                    Pedido(
                        id = id,
                        categoriaId = categoriaId,
                        categoria = categoriasMap[categoriaId] ?: "Sem categoria",
                        localizacao = pedido["localizacao"]?.jsonPrimitive?.contentOrNull ?: "",
                        descricao = pedido["descricao"]?.jsonPrimitive?.contentOrNull ?: "",
                        estado = pedido["estado"]?.jsonPrimitive?.contentOrNull ?: "SUBMETIDO",
                        dataCriacao = formatarData(createdAt),
                        fotoUrl = pedido["foto_url"]?.jsonPrimitive?.contentOrNull
                    )
                }
                .filter { pedido ->
                    pedido.estado in estadosPermitidos
                }
                .sortedByDescending { pedido ->
                    pedido.id
                }

        } catch (e: Exception) {
            erroCarregamento = "Não foi possível carregar os pedidos."
        } finally {
            isLoadingPedidos = false
        }
    }

    val pedidosFiltrados = pedidos.filter { pedido ->
        val correspondeEstado =
            estadoSelecionado == "TODOS" || pedido.estado == estadoSelecionado

        val correspondePesquisa =
            pedido.id.toString().contains(pesquisa, ignoreCase = true) ||
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
            tituloTopBar = tituloTopBar,
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

            when {
                isLoadingPedidos -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                erroCarregamento.isNotBlank() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = erroCarregamento,
                            color = Color.Red,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                reloadTrigger++
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF247BFF),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }

                pedidosFiltrados.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mensagemVazia,
                            color = Color(0xFF6B7C93),
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(pedidosFiltrados) { pedido ->
                            PedidoCard(
                                pedido = pedido,
                                podeCancelar = permitirCancelamento &&
                                        (pedido.estado == "SUBMETIDO" || pedido.estado == "EM_ANALISE"),
                                podeEditar = permitirCancelamento &&
                                        (pedido.estado == "SUBMETIDO" || pedido.estado == "EM_ANALISE"),
                                onDetailsClick = {
                                    pedidoSelecionado = pedido
                                },
                                onEditClick = {
                                    pedidoParaEditar = pedido
                                },
                                onCancelClick = {
                                    pedidoParaEliminar = pedido
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (pedidoSelecionado != null) {
        PedidoDetailsDialog(
            pedido = pedidoSelecionado!!,
            onDismiss = {
                pedidoSelecionado = null
            },
            onImageClick = { fotoUrl ->
                fotoAbertaUrl = fotoUrl
            }
        )
    }

    if (fotoAbertaUrl != null) {
        FullImageDialog(
            fotoUrl = fotoAbertaUrl!!,
            onDismiss = {
                fotoAbertaUrl = null
            }
        )
    }

    if (pedidoParaEliminar != null) {
        DeleteRequestDialog(
            pedido = pedidoParaEliminar!!,
            isLoading = isEliminarLoading,
            onDismiss = {
                if (!isEliminarLoading) {
                    pedidoParaEliminar = null
                }
            },
            onConfirm = {
                val pedido = pedidoParaEliminar

                if (pedido != null) {
                    scope.launch {
                        try {
                            isEliminarLoading = true
                            erroCarregamento = ""

                            val user = SupabaseClient.client.auth.currentUserOrNull()

                            if (user == null) {
                                erroCarregamento = "Sessão inválida. Inicia sessão novamente."
                                return@launch
                            }

                            SupabaseClient.client
                                .from("requests")
                                .delete {
                                    filter {
                                        eq("id", pedido.id)
                                        eq("user_id", user.id)
                                    }
                                }

                            pedidoParaEliminar = null
                            reloadTrigger++
                        } catch (e: Exception) {
                            erroCarregamento = "Não foi possível eliminar o pedido."
                        } finally {
                            isEliminarLoading = false
                        }
                    }
                }
            }
        )
    }
    if (pedidoParaEditar != null) {
        EditRequestDialog(
            pedido = pedidoParaEditar!!,
            isLoading = isEditarLoading,
            onDismiss = {
                if (!isEditarLoading) {
                    pedidoParaEditar = null
                }
            },
            onConfirm = { novaLocalizacao, novaDescricao ->
                val pedido = pedidoParaEditar ?: return@EditRequestDialog

                scope.launch {
                    try {
                        isEditarLoading = true
                        erroCarregamento = ""

                        val user = SupabaseClient.client.auth.currentUserOrNull()

                        if (user == null) {
                            erroCarregamento = "Sessão inválida. Inicia sessão novamente."
                            return@launch
                        }

                        SupabaseClient.client
                            .from("requests")
                            .update(
                                buildJsonObject {
                                    put("localizacao", novaLocalizacao.trim())
                                    put("descricao", novaDescricao.trim())
                                }
                            ) {
                                filter {
                                    eq("id", pedido.id)
                                    eq("user_id", user.id)
                                }
                            }

                        pedidoParaEditar = null
                        reloadTrigger++
                    } catch (e: Exception) {
                        erroCarregamento = "Não foi possível atualizar o pedido."
                    } finally {
                        isEditarLoading = false
                    }
                }
            }
        )
    }
}

@Composable
private fun RequestsTopBar(
    nomeUtilizador: String,
    tituloTopBar: String,
    onBackClick: () -> Unit,
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
            text = tituloTopBar,
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
                onDismissRequest = {
                    menuAberto = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Perfil")
                    },
                    onClick = {
                        menuAberto = false
                        onPerfilClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Terminar sessão")
                    },
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
    pedido: Pedido,
    podeCancelar: Boolean,
    podeEditar: Boolean,
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit
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
                        text = "Pedido #${pedido.id}",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pedido.descricao,
                fontSize = 12.sp,
                color = Color(0xFF4B5563)
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

            if (podeEditar) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onEditClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD9EAFE),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Editar pedido",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (podeCancelar) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD6D6),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Cancelar pedido",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
        "CANCELADO" -> Color(0xFFE5E7EB)
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

@Composable
private fun PedidoDetailsDialog(
    pedido: Pedido,
    onDismiss: () -> Unit,
    onImageClick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Pedido #${pedido.id}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                DetailDialogItem(
                    label = "Estado",
                    value = formatarEstado(pedido.estado)
                )

                DetailDialogItem(
                    label = "Categoria",
                    value = pedido.categoria
                )

                DetailDialogItem(
                    label = "Localização",
                    value = pedido.localizacao
                )

                DetailDialogItem(
                    label = "Data de criação",
                    value = pedido.dataCriacao
                )

                DetailDialogItem(
                    label = "Descrição",
                    value = pedido.descricao
                )

                if (!pedido.fotoUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fotografia",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7C93)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AsyncImage(
                        model = pedido.fotoUrl,
                        contentDescription = "Fotografia associada ao pedido",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onImageClick(pedido.fotoUrl)
                            },
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Clica na imagem para ampliar",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7C93)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Fechar",
                    color = Color(0xFF247BFF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun DeleteRequestDialog(
    pedido: Pedido,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Cancelar pedido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "Tens a certeza que queres cancelar o Pedido #${pedido.id}?",
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Esta ação vai eliminar o pedido permanentemente.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7C93)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "A eliminar..." else "Eliminar pedido",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Voltar",
                    color = Color(0xFF247BFF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun DetailDialogItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7C93)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value.ifBlank { "Sem informação" },
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

private fun contarPedidosPorEstado(
    pedidos: List<Pedido>,
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
        "CANCELADO" -> "Cancelado"
        else -> estado
    }
}

private fun formatarData(data: String): String {
    return if (data.length >= 10) {
        val ano = data.substring(0, 4)
        val mes = data.substring(5, 7)
        val dia = data.substring(8, 10)

        "$dia/$mes/$ano"
    } else {
        data
    }
}
@Composable
private fun FullImageDialog(
    fotoUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fotografia",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "✕",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable {
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Fotografia ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
@Composable
private fun EditRequestDialog(
    pedido: Pedido,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var localizacao by remember { mutableStateOf(pedido.localizacao) }
    var descricao by remember { mutableStateOf(pedido.descricao) }
    var erro by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Editar pedido #${pedido.id}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = "Localização",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7C93)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = localizacao,
                    onValueChange = {
                        localizacao = it
                    },
                    enabled = !isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Descrição",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7C93)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = {
                        descricao = it
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                if (erro.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = erro,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (localizacao.isBlank()) {
                        erro = "Indica a localização."
                        return@TextButton
                    }

                    if (descricao.isBlank()) {
                        erro = "Escreve uma descrição."
                        return@TextButton
                    }

                    if (descricao.length < 10) {
                        erro = "A descrição deve ter pelo menos 10 caracteres."
                        return@TextButton
                    }

                    onConfirm(localizacao, descricao)
                },
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "A guardar..." else "Guardar alterações",
                    color = Color(0xFF247BFF),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancelar",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}