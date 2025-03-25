package com.zalerie.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zalerie.viewmodel.AuthViewModel
import com.zalerie.ui.bottomNavigation.saveableNavigate
import com.zalerie.ui.components.CustomButton1
import com.zalerie.ui.components.TextFieldEndTrailingIcon
import com.zalerie.ui.screens.Screens
import com.zalerie.ui.snackbar.SnackbarState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel = koinViewModel()
) {
    val errorMessageFlow = viewModel.errorMessage
    val snackbarState: SnackbarState = koinInject()
    Column(
        modifier = modifier
            .wrapContentSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier,
            text = "Zalerie",
            fontSize = 50.sp,
            letterSpacing = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CustomButton1(
                modifier = Modifier,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                buttonText = "LOGIN",
                letterSpacing = 2.sp,
                fontSize = 15.sp
            ) {
                navHostController.saveableNavigate(route = Screens.LoginScreen)
            }
            CustomButton1(
                modifier = Modifier,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                buttonText = "SIGN UP",
                letterSpacing = 2.sp,
                fontSize = 15.sp
            ) {
                navHostController.saveableNavigate(route = Screens.SignUpScreen)
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
}

@Composable
fun LoginScreenTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textValue: String = "",
    value: (String) -> Unit
) {
    TextFieldEndTrailingIcon(
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        textValue = textValue,
        cornerRadius = 10.dp,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedPlaceholderColor = Color.White,
            unfocusedPlaceholderColor = Color.White
        )
    ) {
        value(it)
    }
}


@Composable
@Preview
fun TestComponent1() {
    AuthScreen(modifier = Modifier)
}