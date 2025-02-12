package com.dressden.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dressden.data.repository.UserRepository
import com.dressden.data.repository.ProductRepository
import com.dressden.data.repository.WishlistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get input data
            val fcmToken = inputData.getString("fcm_token")
            val notificationType = inputData.getString("type")
            val productId = inputData.getString("product_id")
            val orderId = inputData.getString("order_id")

            when {
                // Handle FCM token refresh
                fcmToken != null -> {
                    Timber.d("Updating FCM token: $fcmToken")
                    userRepository.updateFcmToken(fcmToken)
                }

                // Handle product-related notifications
                notificationType == "product" && productId != null -> {
                    Timber.d("Syncing product data: $productId")
                    productRepository.refreshProducts()
                }

                // Handle order-related notifications
                notificationType == "order" && orderId != null -> {
                    Timber.d("Syncing order data: $orderId")
                    // TODO: Implement order sync
                }

                // Handle wishlist-related notifications
                notificationType == "wishlist" -> {
                    Timber.d("Syncing wishlist data")
                    wishlistRepository.syncWishlist()
                }

                // Handle promotion notifications
                notificationType == "promotion" -> {
                    Timber.d("Syncing promotion data")
                    // TODO: Implement promotion sync
                }

                // Handle general data sync
                notificationType == "sync" -> {
                    Timber.d("Performing general data sync")
                    productRepository.refreshProducts()
                    wishlistRepository.syncWishlist()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during notification sync")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val TAG = "NotificationSyncWorker"
    }
}
