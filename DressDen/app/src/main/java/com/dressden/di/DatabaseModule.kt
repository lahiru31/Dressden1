package com.dressden.di

import android.content.Context
import androidx.room.Room
import com.dressden.data.local.AppDatabase
import com.dressden.data.local.CategoryDao
import com.dressden.data.local.ProductDao
import com.dressden.data.local.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dress_den_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideWishlistDao(appDatabase: AppDatabase): WishlistDao {
        return appDatabase.wishlistDao()
    }

    @Provides
    @Singleton
    fun provideGson(): com.google.gson.Gson {
        return com.google.gson.GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideDateConverter(): com.dressden.utils.DateConverter {
        return com.dressden.utils.DateConverter()
    }

    @Provides
    @Singleton
    fun provideListConverter(): com.dressden.utils.ListConverter {
        return com.dressden.utils.ListConverter()
    }

    @Provides
    @Singleton
    fun provideMapConverter(): com.dressden.utils.MapConverter {
        return com.dressden.utils.MapConverter()
    }
}
