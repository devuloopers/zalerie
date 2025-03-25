package com.zalerie.ui.screens.userDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.auth.User
import com.zalerie.ui.bottomNavigation.navigateAndClear
import com.zalerie.ui.bottomNavigation.saveableNavigate
import com.zalerie.ui.components.CustomButton1
import com.zalerie.ui.loadingBar.LoadingBarState
import com.zalerie.ui.screens.Screens
import com.zalerie.ui.screens.login.LoginScreenTextField
import com.zalerie.viewmodel.AuthViewModel
import com.zalerie.viewmodel.UserData
import com.zalerie.viewmodel.UserViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserDetails(
    modifier: Modifier,
    navHostController: NavHostController,
    userViewModel: UserViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    val loadingBarState: LoadingBarState = koinInject()
    val user by authViewModel.user.collectAsState()
    val isUserRegistered by userViewModel.isUserRegistered.collectAsState()

    LaunchedEffect(isUserRegistered) {
        isUserRegistered?.let { registered ->
            if (registered) {
                navHostController.navigateAndClear(
                    route = Screens.HomeScreen,
                    popUpScreen = Screens.UserDetailScreen
                )
                loadingBarState.hideLoading()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .padding(top = 100.dp)
                .align(Alignment.TopCenter),
            text = "Enter Details",
            fontSize = 30.sp,
            color = Color.White,
            letterSpacing = 5.sp,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginScreenTextField(placeholder = "Name", textValue = name) {
                name = it
            }
            Spacer(modifier = Modifier.height(100.dp))
            CustomButton1(
                modifier = Modifier,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                buttonText = "SUBMIT",
                fontSize = 15.sp
            ) {
                loadingBarState.showLoading()
                user?.let {
                    val userData =
                        UserData(
                            uid = it.uid,
                            email = it.email,
                            name = name,
                            createdAt = getCurrentDateTime()
                        )
                    userViewModel.registerUser(userId = it.uid, userData = userData)
                }
            }
        }
    }
}

fun getCurrentDateTime(): String {
    val now = Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date} ${localDateTime.time.hour}:${localDateTime.time.minute}:${localDateTime.time.second}"
}