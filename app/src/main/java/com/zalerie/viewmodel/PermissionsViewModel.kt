package com.zalerie.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalerie.appPermissions.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Idle)
    val permissionState: StateFlow<PermissionState> = _permissionState

    private var requestCount: Int
        get() = sharedPreferences.getInt("permission_request_count", 0)
        set(value) = sharedPreferences.edit().putInt("permission_request_count", value).apply()

    init {
        if (requestCount >= 2) {
            _permissionState.value = PermissionState.PermanentlyDenied
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            requestCount = 0
            _permissionState.emit(PermissionState.Granted)
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            requestCount++
            _permissionState.emit(
                if (requestCount >= 2) PermissionState.PermanentlyDenied
                else PermissionState.Denied
            )
        }
    }

    fun verifyPermissionsFromSettings(context: Context) {
        viewModelScope.launch {
            if (PermissionUtils.hasPermissions(context)) {
                onPermissionGranted()
            } else if (_permissionState.value is PermissionState.PermanentlyDenied) {
                if (PermissionUtils.hasPermissions(context)) {
                    onPermissionGranted()
                }
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _permissionState.emit(PermissionState.Idle)
        }
    }
}

sealed class PermissionState {
    data object Idle : PermissionState()
    data object Granted : PermissionState()
    data object Denied : PermissionState()
    data object PermanentlyDenied : PermissionState()
}