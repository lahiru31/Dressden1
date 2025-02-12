package com.dressden.di

import com.dressden.data.local.AppDatabase
import com.dressden.data.local.UserPreferences
import com.dressden.data.local.WishlistDao
import com.dressden.data.remote.ApiService
import com.dressden.data.repository.CategoryRepository
import com.dressden.data.repository.ProductRepository
import com.dressden.data.repository.UserRepository
import com.dressden.data.repository.WishlistRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        apiService: ApiService,
        userPreferences: UserPreferences
    ): UserRepository {
        return UserRepository(firebaseAuth, apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): CategoryRepository {
        return CategoryRepository(apiService, appDatabase)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
        appDatabase: AppDatabase,
        wishlistDao: WishlistDao
    ): ProductRepository {
        return ProductRepository(apiService, appDatabase, wishlistDao)
    }

    @Provides
    @Singleton
    fun provideWishlistRepository(
        apiService: ApiService,
        wishlistDao: WishlistDao,
        productRepository: ProductRepository
    ): WishlistRepository {
        return WishlistRepository(apiService, wishlistDao, productRepository)
    }
}
