package com.example.campusmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campusmanager.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tipoPerfil by remember { mutableStateOf("UTILIZADOR") }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        Text(
            text = "🌐",
            fontSize = 22.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Registar",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier.width(280.dp)
            ) {
                Text(
                    text = "Nome",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    enabled = !isLoading,
                    singleLine = true,
                    leadingIcon = {
                        Text("♙", fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    enabled = !isLoading,
                    singleLine = true,
                    leadingIcon = {
                        Text("✉️", fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Nome de utilizador",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    enabled = !isLoading,
                    singleLine = true,
                    leadingIcon = {
                        Text("♙", fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    enabled = !isLoading,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Text("🔒", fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipo de perfil",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = tipoPerfil == "UTILIZADOR",
                        onClick = { tipoPerfil = "UTILIZADOR" },
                        label = { Text("Utilizador") },
                        enabled = !isLoading
                    )

                    FilterChip(
                        selected = tipoPerfil == "ADMIN",
                        onClick = { tipoPerfil = "ADMIN" },
                        label = { Text("Administrador") },
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (erro.isNotBlank()) {
                    Text(
                        text = erro,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (mensagem.isNotBlank()) {
                    Text(
                        text = mensagem,
                        color = Color(0xFF15803D),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
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
                            if (
                                nome.isBlank() ||
                                username.isBlank() ||
                                email.isBlank() ||
                                password.isBlank()
                            ) {
                                erro = "Preenche todos os campos."
                                return@Button
                            }

                            if (!email.contains("@") || !email.contains(".")) {
                                erro = "Insere um email válido."
                                return@Button
                            }

                            if (password.length < 6) {
                                erro = "A password deve ter pelo menos 6 caracteres."
                                return@Button
                            }

                            scope.launch {
                                try {
                                    isLoading = true
                                    erro = ""
                                    mensagem = ""

                                    SupabaseClient.client.auth.signUpWith(Email) {
                                        this.email = email.trim()
                                        this.password = password
                                        data = buildJsonObject {
                                            put("nome", nome.trim())
                                            put("username", username.trim())
                                            put("tipo_perfil", tipoPerfil)
                                        }
                                    }

                                    mensagem = "Conta criada com sucesso. Já podes iniciar sessão."
                                } catch (e: Exception) {
                                    erro = if (e.message?.contains("over_email_send_rate_limit") == true) {
                                        "Foram feitas demasiadas tentativas. Aguarda alguns minutos."
                                    } else {
                                        "Não foi possível criar a conta. Verifica os dados."
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF247BFF),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Registar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Já tens conta?",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = onBackClick,
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Entrar",
                            color = Color(0xFF2448D8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}