package com.harissabil.zakatkuy.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Splash : Route()

    @Serializable
    data object Login : Route()

    @Serializable
    data object Register : Route()

    @Serializable
    data object Form : Route()

    @Serializable
    data object Home : Route()

    @Serializable
    data object KalkulatorZakat : Route()

    @Serializable
    data class Chat(
        val chatHistoryId: String? = null,
    ) : Route()

    @Serializable
    data class Payment(
        val url: String,
    ) : Route()

    @Serializable
    data object Maps : Route()

    @Serializable
    data object History : Route()

    @Serializable
    data object AmilHome : Route()

    @Serializable
    data object Catetin : Route()

    @Serializable
    data object DashboardAmil : Route()
}