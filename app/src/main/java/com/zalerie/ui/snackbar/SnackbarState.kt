package com.zalerie.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.dsl.module

class SnackbarState(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) {
    fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onDismissed: (() -> Unit)? = null
    ) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )

            if (result == androidx.compose.material3.SnackbarResult.Dismissed) {
                onDismissed?.invoke()
            }
        }
    }
}

val snackbarModule = module {
    single { SnackbarHostState() }
    single { CoroutineScope(Dispatchers.Main + SupervisorJob()) }
    single { SnackbarState(get(), get()) }
}