package com.zalerie.di

import android.app.Application
import com.zalerie.appPermissions.permissionModule
import com.zalerie.ui.loadingBar.loadingBarModule
import com.zalerie.ui.snackbar.snackbarModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RootDI : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RootDI)
            modules(
                permissionModule,
                firebaseAuthModule,
                snackbarModule,
                userFirebaseModule,
                loadingBarModule,
                firebaseStorage,
                roomDBModule,
                mediaItemModule
            )
        }
    }
}