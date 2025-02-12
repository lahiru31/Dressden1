package com.dressden.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dressden.data.model.Category
import com.dressden.data.model.Product
import com.dressden.data.model.Wishlist
import com.dressden.utils.DateConverter
import com.dressden.utils.ListConverter
import com.dressden.utils.MapConverter

@Database(
    entities = [
        Product::class,
        Category::class,
        Wishlist::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    MapConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        private const val DATABASE_NAME = "dress_den_db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .addCallback(object : RoomDatabase.Callback() {
                // Add any database callbacks here if needed
            })
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
