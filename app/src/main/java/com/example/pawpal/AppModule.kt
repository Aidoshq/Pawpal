package com.example.pawpal

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }

    viewModel {
        LoginViewModel(get())
    }
}