package com.dressden.data.repository

import com.dressden.data.local.WishlistDao
import com.dressden.data.model.Product
import com.dressden.data.model.Wishlist
import com.dressden.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date

@Singleton
class WishlistRepository @Inject constructor(
    private val apiService: ApiService,
    private val wishlistDao: WishlistDao,
    private val productRepository: ProductRepository
) {
    suspend fun addToWishlist(productId: String) = withContext(Dispatchers.IO) {
        try {
            // Add to remote wishlist
            apiService.addToWishlist(productId)
            
            // Add to local wishlist
            wishlistDao.insert(
                Wishlist(
                    productId = productId,
                    addedAt = Date()
                )
            )
        } catch (e: Exception) {
            // If remote fails, still add to local wishlist
            wishlistDao.insert(
                Wishlist(
                    productId = productId,
                    addedAt = Date(),
                    syncPending = true
                )
            )
        }
    }

    suspend fun removeFromWishlist(productId: String) = withContext(Dispatchers.IO) {
        try {
            // Remove from remote wishlist
            apiService.removeFromWishlist(productId)
            
            // Remove from local wishlist
            wishlistDao.delete(productId)
        } catch (e: Exception) {
            // If remote fails, mark for sync
            wishlistDao.markForDeletion(productId)
        }
    }

    suspend fun isProductInWishlist(productId: String): Boolean = withContext(Dispatchers.IO) {
        wishlistDao.isProductInWishlist(productId)
    }

    fun getWishlistProducts(): Flow<List<Product>> {
        return wishlistDao.getWishlistFlow().map { wishlistItems ->
            wishlistItems.mapNotNull { wishlist ->
                productRepository.getProduct(wishlist.productId)
            }
        }
    }

    suspend fun syncWishlist() = withContext(Dispatchers.IO) {
        try {
            // Get pending additions
            val pendingAdditions = wishlistDao.getPendingAdditions()
            pendingAdditions.forEach { wishlist ->
                try {
                    apiService.addToWishlist(wishlist.productId)
                    wishlistDao.markSynced(wishlist.productId)
                } catch (e: Exception) {
                    // Keep sync pending if failed
                }
            }

            // Get pending deletions
            val pendingDeletions = wishlistDao.getPendingDeletions()
            pendingDeletions.forEach { wishlist ->
                try {
                    apiService.removeFromWishlist(wishlist.productId)
                    wishlistDao.delete(wishlist.productId)
                } catch (e: Exception) {
                    // Keep deletion pending if failed
                }
            }

            // Get remote wishlist and sync with local
            val remoteWishlist = apiService.getWishlist()
            val localWishlist = wishlistDao.getAll()

            // Add missing items locally
            remoteWishlist.forEach { remoteItem ->
                if (localWishlist.none { it.productId == remoteItem.productId }) {
                    wishlistDao.insert(
                        Wishlist(
                            productId = remoteItem.productId,
                            addedAt = remoteItem.addedAt
                        )
                    )
                }
            }

            // Remove extra items locally
            localWishlist.forEach { localItem ->
                if (remoteWishlist.none { it.productId == localItem.productId }) {
                    wishlistDao.delete(localItem.productId)
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }
}
