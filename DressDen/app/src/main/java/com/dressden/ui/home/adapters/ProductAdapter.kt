package com.dressden.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dressden.R
import com.dressden.data.model.Product
import com.dressden.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onProductClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Product) -> Unit,
        private val onFavoriteClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

        fun bind(product: Product) {
            binding.apply {
                // Set basic product info
                productName.text = product.name
                price.text = currencyFormatter.format(product.price)

                // Load product image
                Glide.with(productImage)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(productImage)

                // Handle discount if present
                if (product.originalPrice != null && product.originalPrice > product.price) {
                    originalPrice.apply {
                        visibility = View.VISIBLE
                        text = currencyFormatter.format(product.originalPrice)
                    }
                    
                    val discountPercentage = ((product.originalPrice - product.price) / product.originalPrice * 100).toInt()
                    discountBadge.apply {
                        visibility = View.VISIBLE
                        text = root.context.getString(R.string.discount_percentage, discountPercentage)
                    }
                } else {
                    originalPrice.visibility = View.GONE
                    discountBadge.visibility = View.GONE
                }

                // Set rating
                ratingBar.rating = product.rating
                ratingCount.text = root.context.getString(R.string.rating_count, product.reviews)

                // Set favorite button state and click listener
                favoriteButton.setImageResource(
                    if (product.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
                favoriteButton.setOnClickListener {
                    onFavoriteClick(product)
                }

                // Set click listener for the whole item
                root.setOnClickListener {
                    onProductClick(product)
                }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
