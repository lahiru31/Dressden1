package com.dressden.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dressden.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getById(productId: String): Product?

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    suspend fun getProductsByCategory(categoryId: String): List<Product>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getProductsByCategoryFlow(categoryId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isNewArrival = 1 ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getNewArrivals(limit: Int): List<Product>

    @Query("SELECT * FROM products WHERE isPopular = 1 ORDER BY reviews DESC LIMIT :limit")
    suspend fun getPopularItems(limit: Int): List<Product>

    @Query("SELECT * FROM products WHERE isFeatured = 1 ORDER BY createdAt DESC")
    suspend fun getFeaturedProducts(): List<Product>

    @Query("SELECT * FROM products WHERE name LIKE :query OR description LIKE :query")
    suspend fun searchProducts(query: String): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT * FROM products WHERE stockQuantity > 0 ORDER BY createdAt DESC")
    suspend fun getInStockProducts(): List<Product>

    @Query("SELECT * FROM products WHERE originalPrice > price ORDER BY (originalPrice - price) DESC")
    suspend fun getDiscountedProducts(): List<Product>

    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    suspend fun getProductCountByCategory(categoryId: String): Int

    @Transaction
    suspend fun updateProducts(products: List<Product>) {
        deleteAll()
        insertAll(products)
    }

    @Query("""
        SELECT * FROM products 
        WHERE categoryId = :categoryId 
        AND price BETWEEN :minPrice AND :maxPrice 
        ORDER BY 
            CASE 
                WHEN :sortBy = 'price_asc' THEN price 
                WHEN :sortBy = 'price_desc' THEN -price 
                WHEN :sortBy = 'rating' THEN -rating 
                ELSE createdAt 
            END
    """)
    suspend fun getFilteredProducts(
        categoryId: String,
        minPrice: Double,
        maxPrice: Double,
        sortBy: String
    ): List<Product>
}
