package com.zalerie.ui.bottomNavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zalerie.ui.screens.Screens
import com.zalerie.ui.screens.home.HomeScreen
import com.zalerie.ui.screens.login.AuthScreen
import com.zalerie.ui.screens.login.LoginScreen
import com.zalerie.ui.screens.login.SignUpScreen
import com.zalerie.ui.screens.splash.SplashScreen
import com.zalerie.ui.screens.userDetails.UserDetails

@Composable
fun NavHostController(modifier: Modifier, navHostController: NavHostController) {
    NavHost(
        modifier = modifier
            .fillMaxSize(),
        navController = navHostController,
        startDestination = Screens.SplashScreen,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(700)
            )
        }
    ) {
        composable<Screens.SplashScreen> {
            SplashScreen(modifier = Modifier, navHostController = navHostController)
        }
        composable<Screens.AuthScreen> {
            AuthScreen(modifier = Modifier, navHostController)
        }
        composable<Screens.SignUpScreen> {
            SignUpScreen(modifier = Modifier, navHostController = navHostController)
        }
        composable<Screens.LoginScreen> {
            LoginScreen(modifier = Modifier, navHostController = navHostController)
        }
        composable<Screens.UserDetailScreen> {
            UserDetails(modifier = Modifier, navHostController = navHostController)
        }
        composable<Screens.HomeScreen> {
            HomeScreen(modifier = Modifier, navHostController = navHostController)
        }
        composable<Screens.ProfileScreen> {

        }
    }
}

fun NavController.saveableNavigate(route: Screens) {
    this.navigate(route) {
        popUpTo(this@saveableNavigate.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateAndClear(route: Screens, popUpScreen: Screens) {
    this.navigate(route) {
        popUpTo(popUpScreen) { inclusive = true }
    }
}
fun NavController.navigateAndClearAll(route: Screens) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}