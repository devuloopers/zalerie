package com.zalerie.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zalerie.ui.bottomNavigation.navigateAndClear
import com.zalerie.ui.screens.Screens
import com.zalerie.viewmodel.AuthViewModel
import com.zalerie.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    modifier: Modifier,
    authViewModel: AuthViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    navHostController: NavHostController = rememberNavController()
) {
    val user by authViewModel.user.collectAsState()
    val isUserRegistered by userViewModel.isUserRegistered.collectAsState()

    LaunchedEffect(user) {
        delay(5000)
        user?.uid?.let { uid ->
            userViewModel.checkUserRegistration(uid)
        } ?: run {
            navHostController.navigateAndClear(
                route = Screens.AuthScreen,
                popUpScreen = Screens.SplashScreen
            )
        }
    }
    LaunchedEffect(isUserRegistered) {
        isUserRegistered?.let { registered ->
            val destination = when {
                registered -> Screens.HomeScreen
                else -> Screens.UserDetailScreen
            }
            navHostController.navigateAndClear(
                route = destination,
                popUpScreen = Screens.SplashScreen
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier
                .fillMaxSize()
                .background(color = Color(0xFFEF0014))
        )
        Text(
            modifier = Modifier,
            text = "Zalerie",
            fontSize = 60.sp,
            letterSpacing = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            text = "∞",
            fontSize = 50.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
@Preview
private fun Test() {
    SplashScreen(modifier = Modifier)
}