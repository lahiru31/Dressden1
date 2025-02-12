package com.dressden.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database.apply {
            // Enable offline persistence
            setPersistenceEnabled(true)
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthStateListener(auth: FirebaseAuth): FirebaseAuthStateListener {
        return FirebaseAuthStateListener(auth)
    }
}

class FirebaseAuthStateListener @Inject constructor(private val auth: FirebaseAuth) {
    
    private var authStateListener: ((FirebaseAuth) -> Unit)? = null

    fun startListening(listener: (FirebaseAuth) -> Unit) {
        authStateListener = listener
        auth.addAuthStateListener(listener)
    }

    fun stopListening() {
        authStateListener?.let { listener ->
            auth.removeAuthStateListener(listener)
            authStateListener = null
        }
    }
}
