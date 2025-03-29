package com.zalerie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil3.util.CoilUtils
import com.zalerie.ui.bottomNavigation.NavHostController
import com.zalerie.ui.loadingBar.LoadingBar
import com.zalerie.ui.snackbar.CustomSnackbar
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            val snackbarHostState: SnackbarHostState = koinInject()
            //val loadingBarState: LoadingBarState = koinInject()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {},
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        CustomSnackbar(snackbarData = it)
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(R.drawable.loginbg),
                        contentScale = ContentScale.Crop,
                        contentDescription = ""
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f))
                            .fillMaxSize()
                    )
                    NavHostController(
                        modifier = Modifier.background(Color.Transparent),
                        navHostController = navHostController
                    )
                }
            }
            LoadingBar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}