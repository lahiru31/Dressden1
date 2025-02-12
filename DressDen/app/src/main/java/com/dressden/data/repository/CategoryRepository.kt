package com.dressden.data.repository

import com.dressden.data.model.Category
import com.dressden.data.remote.ApiService
import com.dressden.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) {
    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            val remoteCategories = apiService.getCategories()
            // Cache the categories
            appDatabase.categoryDao().insertAll(remoteCategories)
            remoteCategories
        } catch (e: Exception) {
            // If network fails, load from cache
            val cachedCategories = appDatabase.categoryDao().getAll()
            if (cachedCategories.isNotEmpty()) {
                cachedCategories
            } else {
                // If cache is empty, return default categories
                Category.getDefaultCategories()
            }
        }
    }

    suspend fun getCategoryById(categoryId: String): Category? = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            apiService.getCategory(categoryId)
        } catch (e: Exception) {
            // If network fails, load from cache
            appDatabase.categoryDao().getById(categoryId)
        }
    }

    suspend fun getFeaturedCategories(): List<Category> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            val remoteCategories = apiService.getFeaturedCategories()
            // Cache the categories
            appDatabase.categoryDao().insertAll(remoteCategories)
            remoteCategories
        } catch (e: Exception) {
            // If network fails, load from cache
            val cachedCategories = appDatabase.categoryDao().getFeaturedCategories()
            if (cachedCategories.isNotEmpty()) {
                cachedCategories
            } else {
                // If cache is empty, return default featured categories
                Category.getDefaultCategories().filter { it.featured }
            }
        }
    }

    suspend fun getSubcategories(parentCategoryId: String): List<Category> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            val remoteCategories = apiService.getSubcategories(parentCategoryId)
            // Cache the categories
            appDatabase.categoryDao().insertAll(remoteCategories)
            remoteCategories
        } catch (e: Exception) {
            // If network fails, load from cache
            appDatabase.categoryDao().getSubcategories(parentCategoryId)
        }
    }

    suspend fun searchCategories(query: String): List<Category> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            apiService.searchCategories(query)
        } catch (e: Exception) {
            // If network fails, search in cache
            appDatabase.categoryDao().searchCategories("%$query%")
        }
    }

    suspend fun refreshCategories() = withContext(Dispatchers.IO) {
        try {
            val remoteCategories = apiService.getCategories()
            appDatabase.categoryDao().insertAll(remoteCategories)
        } catch (e: Exception) {
            throw e
        }
    }
}
