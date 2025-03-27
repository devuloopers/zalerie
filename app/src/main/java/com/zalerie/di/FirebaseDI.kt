package com.zalerie.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zalerie.firebase.FirebaseStorageRepository
import com.zalerie.firebase.FirebaseStorageRepositoryImpl
import com.zalerie.models.UploadViewModel
import com.zalerie.repository.AuthRepository
import com.zalerie.repository.UserRepository
import com.zalerie.viewmodel.AuthViewModel
import com.zalerie.viewmodel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val firebaseAuthModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { AuthRepository(get()) }
    viewModel { AuthViewModel(get()) }
}

val userFirebaseModule = module {
    single { UserRepository(get()) }
    viewModel { UserViewModel(get()) }
}

val firebaseStorage = module {
    single<FirebaseStorageRepository> { FirebaseStorageRepositoryImpl(get(), get(), get()) }
    viewModel { UploadViewModel(get()) }
}