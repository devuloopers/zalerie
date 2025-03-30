package com.zalerie.ui.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.dsl.module

class DialogWindowState {
    var showDialog by mutableStateOf(false)
        private set

    fun showDialogAction() {
        showDialog = true
    }

    fun hideDialog() {
        showDialog = false
    }
}

val dialogModule = module {
    single { DialogWindowState() }
}