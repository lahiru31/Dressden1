package com.dressden.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dressden.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun getAllFlow(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getById(categoryId: String): Category?

    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL ORDER BY `order` ASC")
    suspend fun getMainCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId ORDER BY `order` ASC")
    suspend fun getSubcategories(parentId: String): List<Category>

    @Query("SELECT * FROM categories WHERE featured = 1 ORDER BY `order` ASC")
    suspend fun getFeaturedCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE name LIKE :query OR description LIKE :query")
    suspend fun searchCategories(query: String): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun delete(categoryId: String)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Transaction
    suspend fun updateCategories(categories: List<Category>) {
        deleteAll()
        insertAll(categories)
    }

    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT * FROM categories WHERE id = :categoryId
            UNION ALL
            SELECT c.* FROM categories c
            INNER JOIN category_tree ct ON c.parentCategoryId = ct.id
        )
        SELECT * FROM category_tree
    """)
    suspend fun getCategoryWithChildren(categoryId: String): List<Category>

    @Query("""
        WITH RECURSIVE category_path AS (
            SELECT * FROM categories WHERE id = :categoryId
            UNION ALL
            SELECT c.* FROM categories c
            INNER JOIN category_path cp ON c.id = cp.parentCategoryId
        )
        SELECT * FROM category_path
    """)
    suspend fun getCategoryPath(categoryId: String): List<Category>

    @Query("SELECT COUNT(*) FROM categories WHERE parentCategoryId = :categoryId")
    suspend fun getSubcategoryCount(categoryId: String): Int
}
