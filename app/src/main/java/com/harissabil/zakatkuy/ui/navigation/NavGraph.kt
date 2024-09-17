package com.harissabil.zakatkuy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.harissabil.zakatkuy.ui.screen.amil_home.AmilHomeScreen
import com.harissabil.zakatkuy.ui.screen.catetin.CatetinScreen
import com.harissabil.zakatkuy.ui.screen.chat.ChatScreen
import com.harissabil.zakatkuy.ui.screen.dashboard_amil.DashboardAmilScreen
import com.harissabil.zakatkuy.ui.screen.form.FormScreen
import com.harissabil.zakatkuy.ui.screen.history.HistoryScreen
import com.harissabil.zakatkuy.ui.screen.home.HomeScreen
import com.harissabil.zakatkuy.ui.screen.login.LoginScreen
import com.harissabil.zakatkuy.ui.screen.maps.MapsScreen
import com.harissabil.zakatkuy.ui.screen.payment.PaymentScreen
import com.harissabil.zakatkuy.ui.screen.register.RegisterScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: Route,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Splash> {

        }
        composable<Route.Login> {
            LoginScreen(
                onNavigateToRegisterScreen = { navController.navigate(Route.Register) },
                onNavigateToHomeScreen = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                },
                onNavigateToFormScreen = {
                    navController.navigate(Route.Form) {
                        popUpTo(Route.Form) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onNavigateToLoginScreen = { navController.navigate(Route.Login) },
                onNavigateToHomeScreen = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                },
                onNavigateToFormScreen = {
                    navController.navigate(Route.Form) {
                        popUpTo(Route.Form) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Form> {
            FormScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onNavigateToFormScreen = {
                    navController.navigate(Route.Form) {
                        popUpTo(Route.Form) { inclusive = true }
                    }
                },
                onNavigateToChatScreen = { chatHistoryId ->
                    navController.navigate(Route.Chat(chatHistoryId))
                },
                onNavigateToMapsScreen = { navController.navigate(Route.Maps) },
                onNavigateToHistoryScreen = { navController.navigate(Route.History) },
                onNavigateToAmilHomeScreen = {
                    navController.navigate(Route.AmilHome) {
                        popUpTo(Route.AmilHome) {
                            inclusive = true
                            saveState = true
                        }
                    }
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToPaymentScreen = { url ->
                    navController.navigate(Route.Payment(url))
                }
            )
        }

        composable<Route.KalkulatorZakat> {

        }

        composable<Route.Chat> {
            val args = it.toRoute<Route.Chat>()
            ChatScreen(
                chatHistoryId = args.chatHistoryId,
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPaymentScreen = { url ->
                    navController.navigate(Route.Payment(url))
                }
            )
        }

        composable<Route.Payment> {
            val args = it.toRoute<Route.Payment>()
            PaymentScreen(
                url = args.url,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable<Route.Maps> {
            MapsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable<Route.History> {
            HistoryScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable<Route.AmilHome> {
            AmilHomeScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) {
                            inclusive = true
                            saveState = true
                        }
                    }
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToCatetinScreen = { navController.navigate(Route.Catetin) },
                onNavigateToDashboardAmilScreen = { navController.navigate(Route.DashboardAmil) }
            )
        }

        composable<Route.Catetin> {
            CatetinScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable<Route.DashboardAmil> {
            DashboardAmilScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}