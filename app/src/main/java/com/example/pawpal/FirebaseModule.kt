package com.example.pawpal

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val firebaseModule = module {

    single { FirebaseAuth.getInstance() }

    single { FirebaseFirestore.getInstance() }

    single { FirebaseAnalytics.getInstance(androidContext()) }
}