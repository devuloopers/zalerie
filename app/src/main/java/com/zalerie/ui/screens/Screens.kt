package com.zalerie.ui.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object SplashScreen: Screens()
    @Serializable
    data object AuthScreen: Screens()
    @Serializable
    data object SignUpScreen: Screens()
    @Serializable
    data object LoginScreen: Screens()
    @Serializable
    data object UserDetailScreen: Screens()
    @Serializable
    data object HomeScreen: Screens()
    @Serializable
    data object ProfileScreen: Screens()
}