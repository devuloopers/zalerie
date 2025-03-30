package com.zalerie.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zalerie.navHost.navigateAndClearAll
import com.zalerie.ui.components.CustomButton1
import com.zalerie.ui.loadingBar.LoadingBarState
import com.zalerie.ui.screens.Screens
import com.zalerie.ui.snackbar.SnackbarState
import com.zalerie.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: AuthViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val errorMessageFlow = viewModel.errorMessage
    val snackbarState: SnackbarState = koinInject()
    val scope = rememberCoroutineScope()
    val user by authViewModel.user.collectAsState()
    val loadingBarState: LoadingBarState = koinInject()

    LaunchedEffect(Unit) {
        errorMessageFlow.collectLatest { message ->
            snackbarState.showMessage(message)
            if (loadingBarState.isLoading) {
                loadingBarState.hideLoading()
            }
        }
    }
    LaunchedEffect(user) {
        delay(5000)
        user?.uid?.let { uid ->
            if (uid.isNotBlank()) {
                navHostController.navigateAndClearAll(
                    route = Screens.UserDetailScreen
                )
                loadingBarState.hideLoading()
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier,
                text = "Sign Up",
                fontSize = 30.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.fillMaxHeight(.15f))
            LoginScreenTextField(
                placeholder = "Email",
                textValue = email
            ) { email = it }

            Spacer(modifier = Modifier.height(30.dp))
            LoginScreenTextField(
                placeholder = "Set Password",
                textValue = password,
                visualTransformation = PasswordVisualTransformation()
            ) { password = it }

            Spacer(modifier = Modifier.height(30.dp))
            LoginScreenTextField(
                placeholder = "Confirm Password",
                textValue = confirmPassword,
                visualTransformation = PasswordVisualTransformation()
            ) { confirmPassword = it }

            Spacer(modifier = Modifier.height(100.dp))
            CustomButton1(
                modifier = Modifier,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                buttonText = "SIGN UP",
                letterSpacing = 2.sp,
                fontSize = 15.sp
            ) {
                if (!isValidEmail(email = email)) {
                    snackbarState.showMessage("Invalid Email")
                    return@CustomButton1
                }
                if (password.length < 5) {
                    snackbarState.showMessage("$password Password must be at least 6 characters long.")
                    return@CustomButton1
                }
                if (password == confirmPassword) {
                    loadingBarState.showLoading()
                    scope.launch {
                        viewModel.signUp(email = email, password = password)
                    }
                } else {
                    snackbarState.showMessage("Password and confirm password don't match")
                }
            }
        }
    }
}