package com.dressden.data.repository

import com.dressden.data.model.User
import com.dressden.data.remote.ApiService
import com.dressden.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let { mapFirebaseUser(it) }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user?.let { mapFirebaseUser(it) }
            if (user != null) {
                // Fetch additional user data from backend
                val userData = apiService.getUserProfile(user.id)
                // Save user preferences
                userPreferences.saveUserId(user.id)
                Result.success(userData)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Update display name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Create user profile in backend
                val user = mapFirebaseUser(firebaseUser)
                apiService.createUserProfile(user)
                
                // Save user preferences
                userPreferences.saveUserId(user.id)
                
                Result.success(user)
            } else {
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        userPreferences.clearUserId()
    }

    suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val updatedUser = apiService.updateUserProfile(user)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName,
            phoneNumber = firebaseUser.phoneNumber,
            profileImageUrl = firebaseUser.photoUrl?.toString(),
            address = null // Address will be fetched from backend
        )
    }
}
