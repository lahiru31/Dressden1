package com.dressden.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dressden.data.model.Wishlist
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getWishlistFlow(): Flow<List<Wishlist>>

    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    suspend fun getAll(): List<Wishlist>

    @Query("SELECT * FROM wishlist WHERE syncPending = 1 AND deletePending = 0")
    suspend fun getPendingAdditions(): List<Wishlist>

    @Query("SELECT * FROM wishlist WHERE deletePending = 1")
    suspend fun getPendingDeletions(): List<Wishlist>

    @Query("SELECT productId FROM wishlist WHERE deletePending = 0")
    suspend fun getWishlistProductIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wishlist: Wishlist)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("UPDATE wishlist SET syncPending = 0 WHERE productId = :productId")
    suspend fun markSynced(productId: String)

    @Query("UPDATE wishlist SET deletePending = 1 WHERE productId = :productId")
    suspend fun markForDeletion(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId AND deletePending = 0)")
    suspend fun isProductInWishlist(productId: String): Boolean

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()

    @Query("SELECT COUNT(*) FROM wishlist WHERE deletePending = 0")
    fun getWishlistCount(): Flow<Int>

    @Query("UPDATE wishlist SET userId = :userId WHERE userId IS NULL")
    suspend fun assignUserToWishlist(userId: String)

    @Query("DELETE FROM wishlist WHERE userId = :userId")
    suspend fun clearUserWishlist(userId: String)
}
