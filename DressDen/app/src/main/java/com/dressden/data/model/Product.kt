package com.dressden.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val images: List<String> = listOf(),
    val categoryId: String,
    val subcategoryId: String? = null,
    val brand: String? = null,
    val rating: Float = 0f,
    val reviews: Int = 0,
    val sizes: List<String> = listOf(),
    val colors: List<String> = listOf(),
    val stockQuantity: Int = 0,
    val isFavorite: Boolean = false,
    val isNewArrival: Boolean = false,
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false,
    val discount: Int? = null,
    val tags: List<String> = listOf(),
    val specifications: Map<String, String> = mapOf(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable {
    
    val isInStock: Boolean
        get() = stockQuantity > 0

    val hasDiscount: Boolean
        get() = originalPrice != null && originalPrice > price

    val discountPercentage: Int?
        get() = if (hasDiscount && originalPrice != null) {
            ((originalPrice - price) / originalPrice * 100).toInt()
        } else {
            null
        }

    companion object {
        fun createSampleProducts() = listOf(
            Product(
                id = "1",
                name = "Floral Summer Dress",
                description = "Beautiful floral dress perfect for summer",
                price = 49.99,
                originalPrice = 69.99,
                imageUrl = "https://example.com/dress1.jpg",
                categoryId = Category.WOMEN,
                brand = "Summer Fashion",
                rating = 4.5f,
                reviews = 128,
                sizes = listOf("XS", "S", "M", "L", "XL"),
                colors = listOf("Blue", "Pink", "White"),
                stockQuantity = 50,
                isNewArrival = true
            ),
            Product(
                id = "2",
                name = "Classic Men's Suit",
                description = "Elegant classic suit for formal occasions",
                price = 299.99,
                imageUrl = "https://example.com/suit1.jpg",
                categoryId = Category.MEN,
                brand = "Elite Suits",
                rating = 4.8f,
                reviews = 85,
                sizes = listOf("48", "50", "52", "54"),
                colors = listOf("Black", "Navy", "Gray"),
                stockQuantity = 25,
                isPopular = true
            ),
            Product(
                id = "3",
                name = "Kids' Denim Overalls",
                description = "Comfortable and durable denim overalls for kids",
                price = 34.99,
                originalPrice = 44.99,
                imageUrl = "https://example.com/overalls1.jpg",
                categoryId = Category.KIDS,
                brand = "Kids Comfort",
                rating = 4.3f,
                reviews = 62,
                sizes = listOf("2T", "3T", "4T", "5T"),
                colors = listOf("Blue", "Light Blue"),
                stockQuantity = 40,
                isNewArrival = true
            )
        )
    }
}
