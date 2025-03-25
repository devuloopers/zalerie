package com.zalerie.ui.loadingBar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.koin.dsl.module

class LoadingBarState {
    var isLoading by mutableStateOf(false)
        private set

    fun showLoading() {
        isLoading = true
    }

    fun hideLoading() {
        isLoading = false
    }
}

val loadingBarModule = module {
    single { LoadingBarState() }
}