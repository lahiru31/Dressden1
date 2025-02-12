package com.dressden.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val iconUrl: String?,
    val itemCount: Int,
    val parentCategoryId: String? = null,
    val subCategories: List<Category> = emptyList(),
    val featured: Boolean = false,
    val order: Int = 0
) : Parcelable {
    
    companion object {
        const val WOMEN = "women"
        const val MEN = "men"
        const val KIDS = "kids"
        const val ACCESSORIES = "accessories"
        const val SHOES = "shoes"
        
        fun getDefaultCategories() = listOf(
            Category(
                id = WOMEN,
                name = "Women",
                description = "Women's Fashion",
                imageUrl = null,
                iconUrl = null,
                itemCount = 0,
                featured = true,
                order = 1
            ),
            Category(
                id = MEN,
                name = "Men",
                description = "Men's Fashion",
                imageUrl = null,
                iconUrl = null,
                itemCount = 0,
                featured = true,
                order = 2
            ),
            Category(
                id = KIDS,
                name = "Kids",
                description = "Kids' Fashion",
                imageUrl = null,
                iconUrl = null,
                itemCount = 0,
                featured = true,
                order = 3
            ),
            Category(
                id = ACCESSORIES,
                name = "Accessories",
                description = "Fashion Accessories",
                imageUrl = null,
                iconUrl = null,
                itemCount = 0,
                featured = true,
                order = 4
            ),
            Category(
                id = SHOES,
                name = "Shoes",
                description = "Footwear",
                imageUrl = null,
                iconUrl = null,
                itemCount = 0,
                featured = true,
                order = 5
            )
        )
    }
}
