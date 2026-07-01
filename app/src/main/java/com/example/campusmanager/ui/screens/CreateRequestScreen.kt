package com.example.campusmanager.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campusmanager.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

data class CategoriaOpcao(
    val id: Long,
    val nome: String
)

@Composable
fun CreateRequestScreen(
    nomeUtilizador: String,
    onPerfilClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var categoriaSelecionada by remember { mutableStateOf<CategoriaOpcao?>(null) }
    var localizacao by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var fotografiaUri by remember { mutableStateOf<Uri?>(null) }

    var erro by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var categorias by remember { mutableStateOf<List<CategoriaOpcao>>(emptyList()) }
    var isLoadingCategorias by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        fotografiaUri = uri
    }

    LaunchedEffect(Unit) {
        try {
            isLoadingCategorias = true
            erro = ""

            val resultado = SupabaseClient.client
                .from("categories")
                .select()

            categorias = resultado
                .decodeList<JsonObject>()
                .mapNotNull { categoria ->
                    val id = categoria["id"]?.jsonPrimitive?.longOrNull
                    val nome = categoria["nome"]?.jsonPrimitive?.contentOrNull

                    if (id != null && nome != null) {
                        CategoriaOpcao(
                            id = id,
                            nome = nome
                        )
                    } else {
                        null
                    }
                }

        } catch (e: Exception) {
            erro = "Não foi possível carregar as categorias."
        } finally {
            isLoadingCategorias = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FC))
    ) {
        CreateRequestTopBar(
            nomeUtilizador = nomeUtilizador,
            onBackClick = onBackClick,
            onPerfilClick = onPerfilClick,
            onLogoutClick = onLogoutClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Criar pedido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Regista uma ocorrência ou pedido no campus",
                fontSize = 13.sp,
                color = Color(0xFF6B7C93)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Categoria",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoadingCategorias) {
                        CircularProgressIndicator()
                    } else if (categorias.isEmpty()) {
                        Text(
                            text = "Não existem categorias disponíveis.",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categorias.forEach { categoria ->
                                CategoryButton(
                                    text = categoria.nome,
                                    selected = categoriaSelecionada?.id == categoria.id,
                                    enabled = !isLoading,
                                    onClick = {
                                        categoriaSelecionada = categoria
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Localização",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    RequestTextField(
                        value = localizacao,
                        onValueChange = { localizacao = it },
                        placeholder = "Ex: Sala A203, Biblioteca, Bloco B...",
                        icon = "📍",
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descrição",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        enabled = !isLoading,
                        placeholder = {
                            Text("Descreve o problema ou pedido")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fotografia",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
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
                            text = if (fotografiaUri == null) {
                                "Escolher fotografia"
                            } else {
                                "Fotografia selecionada"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (fotografiaUri != null) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Imagem pronta para associar ao pedido.",
                            fontSize = 12.sp,
                            color = Color(0xFF15803D)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (erro.isNotBlank()) {
                        Text(
                            text = erro,
                            color = Color.Red,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (mensagem.isNotBlank()) {
                        Text(
                            text = mensagem,
                            color = Color(0xFF15803D),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                if (categoriaSelecionada == null) {
                                    erro = "Seleciona uma categoria."
                                    return@Button
                                }

                                if (localizacao.isBlank()) {
                                    erro = "Indica a localização do pedido."
                                    return@Button
                                }

                                if (descricao.isBlank()) {
                                    erro = "Escreve uma descrição."
                                    return@Button
                                }

                                if (descricao.length < 10) {
                                    erro = "A descrição deve ter pelo menos 10 caracteres."
                                    return@Button
                                }

                                scope.launch {
                                    try {
                                        isLoading = true
                                        erro = ""
                                        mensagem = ""

                                        val user = SupabaseClient.client.auth.currentUserOrNull()

                                        if (user == null) {
                                            erro = "Sessão inválida. Inicia sessão novamente."
                                            isLoading = false
                                            return@launch
                                        }

                                        var fotoUrlFinal: String? = null

                                        if (fotografiaUri != null) {
                                            val bytes = context.contentResolver
                                                .openInputStream(fotografiaUri!!)
                                                ?.use { inputStream ->
                                                    inputStream.readBytes()
                                                }

                                            if (bytes == null) {
                                                erro = "Não foi possível ler a fotografia selecionada."
                                                isLoading = false
                                                return@launch
                                            }

                                            val caminhoFoto = "${user.id}/${System.currentTimeMillis()}.jpg"

                                            val bucket = SupabaseClient.client
                                                .storage
                                                .from("request-photos")

                                            bucket.upload(
                                                path = caminhoFoto,
                                                data = bytes
                                            ) {
                                                upsert = false
                                            }

                                            fotoUrlFinal = bucket.publicUrl(caminhoFoto)
                                        }

                                        val novoPedido = buildJsonObject {
                                            put("user_id", user.id)
                                            put("category_id", categoriaSelecionada!!.id)
                                            put("localizacao", localizacao.trim())
                                            put("descricao", descricao.trim())
                                            put("estado", "SUBMETIDO")

                                            if (fotoUrlFinal != null) {
                                                put("foto_url", fotoUrlFinal)
                                            }
                                        }

                                        SupabaseClient.client
                                            .from("requests")
                                            .insert(novoPedido)

                                        mensagem = "Pedido submetido com sucesso."

                                        categoriaSelecionada = null
                                        localizacao = ""
                                        descricao = ""
                                        fotografiaUri = null
                                    } catch (e: Exception) {
                                        erro = "Não foi possível criar o pedido. Verifica os dados e tenta novamente."
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF247BFF),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "Submeter pedido",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateRequestTopBar(
    nomeUtilizador: String,
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
            text = "CRIAR PEDIDO",
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
private fun CategoryButton(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF247BFF) else Color(0xFFDDE1E7),
            contentColor = if (selected) Color.White else Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier.height(38.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RequestTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: String,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = {
            Text(icon, fontSize = 15.sp)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE0E0E0),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}