package com.example.campusmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campusmanager.data.remote.SupabaseClient
import com.example.campusmanager.ui.screens.AdminHomeScreen
import com.example.campusmanager.ui.screens.CategoriesScreen
import com.example.campusmanager.ui.screens.CreateRequestScreen
import com.example.campusmanager.ui.screens.LoginScreen
import com.example.campusmanager.ui.screens.MyRequestsScreen
import com.example.campusmanager.ui.screens.ProfileScreen
import com.example.campusmanager.ui.screens.RegisterScreen
import com.example.campusmanager.ui.screens.RequestsByStatusScreen
import com.example.campusmanager.ui.screens.UserHomeScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val USER_HOME = "user_home"
    const val ADMIN_HOME = "admin_home"
    const val CREATE_REQUEST = "create_request"
    const val MY_REQUESTS = "my_requests"
    const val CATEGORIES = "categories"
    const val REQUESTS_BY_STATUS = "requests_by_status"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var nomeUtilizador by remember { mutableStateOf("Utilizador") }

    fun terminarSessao() {
        scope.launch {
            SupabaseClient.client.auth.signOut()

            navController.navigate(Routes.LOGIN) {
                popUpTo(0)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = { tipoPerfil, nome ->
                    nomeUtilizador = nome

                    if (tipoPerfil == "ADMIN") {
                        navController.navigate(Routes.ADMIN_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.USER_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.USER_HOME) {
            UserHomeScreen(
                nomeUtilizador = nomeUtilizador,
                onPerfilClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onCreateRequestClick = {
                    navController.navigate(Routes.CREATE_REQUEST)
                },
                onMyRequestsClick = {
                    navController.navigate(Routes.MY_REQUESTS)
                },
                onLogoutClick = {
                    terminarSessao()
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminHomeScreen(
                nomeUtilizador = nomeUtilizador,
                onPerfilClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onCategoriesClick = {
                    navController.navigate(Routes.CATEGORIES)
                },
                onRequestsByStatusClick = {
                    navController.navigate(Routes.REQUESTS_BY_STATUS)
                },
                onLogoutClick = {
                    terminarSessao()
                }
            )
        }

        composable(Routes.CREATE_REQUEST) {
            CreateRequestScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MY_REQUESTS) {
            MyRequestsScreen(
                nomeUtilizador = nomeUtilizador,
                onPerfilClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    terminarSessao()
                }
            )
        }

        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.REQUESTS_BY_STATUS) {
            RequestsByStatusScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                nomeUtilizador = nomeUtilizador,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}