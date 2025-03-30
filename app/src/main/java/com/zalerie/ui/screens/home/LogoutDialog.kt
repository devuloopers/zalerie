package com.zalerie.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zalerie.navHost.navigateAndClearAll
import com.zalerie.ui.screens.Screens
import com.zalerie.viewmodel.MediaViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogoutDialog(navController: NavController, onDismiss: () -> Unit) {
    val mediaViewModel: MediaViewModel = koinViewModel()
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Are you sure you want to log out?",
                fontSize = 20.sp,
                color = Color.Red,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "No",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Yes",
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.clickable {
                        mediaViewModel.logout()
                        navController.navigateAndClearAll(Screens.SplashScreen)
                        onDismiss()
                    }
                )
            }
        }
    }
}