package com.example.campusmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campusmanager.ui.screens.AdminHomeScreen
import com.example.campusmanager.ui.screens.CategoriesScreen
import com.example.campusmanager.ui.screens.CreateRequestScreen
import com.example.campusmanager.ui.screens.LoginScreen
import com.example.campusmanager.ui.screens.MyRequestsScreen
import com.example.campusmanager.ui.screens.RegisterScreen
import com.example.campusmanager.ui.screens.RequestsByStatusScreen
import com.example.campusmanager.ui.screens.UserHomeScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val USER_HOME = "user_home"
    const val ADMIN_HOME = "admin_home"
    const val CREATE_REQUEST = "create_request"
    const val MY_REQUESTS = "my_requests"
    const val CATEGORIES = "categories"
    const val REQUESTS_BY_STATUS = "requests_by_status"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onUserLoginClick = {
                    navController.navigate(Routes.USER_HOME)
                },
                onAdminLoginClick = {
                    navController.navigate(Routes.ADMIN_HOME)
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
                onCreateRequestClick = {
                    navController.navigate(Routes.CREATE_REQUEST)
                },
                onMyRequestsClick = {
                    navController.navigate(Routes.MY_REQUESTS)
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminHomeScreen(
                onCategoriesClick = {
                    navController.navigate(Routes.CATEGORIES)
                },
                onRequestsByStatusClick = {
                    navController.navigate(Routes.REQUESTS_BY_STATUS)
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
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
                onBackClick = {
                    navController.popBackStack()
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
    }
}