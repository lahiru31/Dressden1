package com.dressden.data.repository

import com.dressden.data.model.Product
import com.dressden.data.remote.ApiService
import com.dressden.data.local.AppDatabase
import com.dressden.data.local.WishlistDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val wishlistDao: WishlistDao
) {
    suspend fun getProducts(
        categoryId: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Product> = withContext(Dispatchers.IO) {
        try {
            // Try to fetch from network
            val remoteProducts = apiService.getProducts(categoryId, page, limit)
            // Cache the products
            appDatabase.productDao().insertAll(remoteProducts)
            // Mark favorites
            markFavoriteProducts(remoteProducts)
        } catch (e: Exception) {
            // If network fails, load from cache
            val cachedProducts = if (categoryId != null) {
                appDatabase.productDao().getProductsByCategory(categoryId)
            } else {
                appDatabase.productDao().getAll()
            }
            
            if (cachedProducts.isNotEmpty()) {
                markFavoriteProducts(cachedProducts)
            } else {
                // If cache is empty, return sample products
                Product.createSampleProducts()
            }
        }
    }

    suspend fun getNewArrivals(limit: Int = 10): List<Product> = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = apiService.getNewArrivals(limit)
            appDatabase.productDao().insertAll(remoteProducts)
            markFavoriteProducts(remoteProducts)
        } catch (e: Exception) {
            val cachedProducts = appDatabase.productDao().getNewArrivals(limit)
            if (cachedProducts.isNotEmpty()) {
                markFavoriteProducts(cachedProducts)
            } else {
                Product.createSampleProducts().filter { it.isNewArrival }
            }
        }
    }

    suspend fun getPopularItems(limit: Int = 10): List<Product> = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = apiService.getPopularItems(limit)
            appDatabase.productDao().insertAll(remoteProducts)
            markFavoriteProducts(remoteProducts)
        } catch (e: Exception) {
            val cachedProducts = appDatabase.productDao().getPopularItems(limit)
            if (cachedProducts.isNotEmpty()) {
                markFavoriteProducts(cachedProducts)
            } else {
                Product.createSampleProducts().filter { it.isPopular }
            }
        }
    }

    suspend fun getProduct(productId: String): Product? = withContext(Dispatchers.IO) {
        try {
            val remoteProduct = apiService.getProduct(productId)
            appDatabase.productDao().insert(remoteProduct)
            markFavoriteProducts(listOf(remoteProduct)).firstOrNull()
        } catch (e: Exception) {
            val cachedProduct = appDatabase.productDao().getById(productId)
            cachedProduct?.let { markFavoriteProducts(listOf(it)).firstOrNull() }
        }
    }

    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = apiService.searchProducts(query)
            appDatabase.productDao().insertAll(remoteProducts)
            markFavoriteProducts(remoteProducts)
        } catch (e: Exception) {
            val cachedProducts = appDatabase.productDao().searchProducts("%$query%")
            markFavoriteProducts(cachedProducts)
        }
    }

    fun getProductsFlow(categoryId: String? = null): Flow<List<Product>> {
        return if (categoryId != null) {
            appDatabase.productDao().getProductsByCategoryFlow(categoryId)
        } else {
            appDatabase.productDao().getAllFlow()
        }.map { products ->
            markFavoriteProducts(products)
        }
    }

    private suspend fun markFavoriteProducts(products: List<Product>): List<Product> {
        val favoriteIds = wishlistDao.getWishlistProductIds()
        return products.map { product ->
            product.copy(isFavorite = product.id in favoriteIds)
        }
    }

    suspend fun refreshProducts() = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = apiService.getProducts()
            appDatabase.productDao().insertAll(remoteProducts)
        } catch (e: Exception) {
            throw e
        }
    }
}
