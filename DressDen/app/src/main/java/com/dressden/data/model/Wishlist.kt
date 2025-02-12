package com.dressden.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "wishlist")
data class Wishlist(
    @PrimaryKey
    val productId: String,
    val addedAt: Date,
    val syncPending: Boolean = false,
    val deletePending: Boolean = false,
    val userId: String? = null // For multi-user support
) {
    companion object {
        fun createSampleWishlist() = listOf(
            Wishlist(
                productId = "1",
                addedAt = Date(),
                syncPending = false
            ),
            Wishlist(
                productId = "2",
                addedAt = Date(),
                syncPending = false
            )
        )
    }
}
