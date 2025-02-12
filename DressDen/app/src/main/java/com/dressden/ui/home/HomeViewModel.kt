package com.dressden.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.data.model.Category
import com.dressden.data.model.Product
import com.dressden.data.repository.CategoryRepository
import com.dressden.data.repository.ProductRepository
import com.dressden.data.repository.WishlistRepository
import com.dressden.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _newArrivals = MutableLiveData<List<Product>>()
    val newArrivals: LiveData<List<Product>> = _newArrivals

    private val _popularItems = MutableLiveData<List<Product>>()
    val popularItems: LiveData<List<Product>> = _popularItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = SingleLiveEvent<String>()
    val error: LiveData<String> = _error

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Load data concurrently
                val categoriesDeferred = async { categoryRepository.getCategories() }
                val newArrivalsDeferred = async { productRepository.getNewArrivals() }
                val popularItemsDeferred = async { productRepository.getPopularItems() }

                // Wait for all data to be loaded
                val categories = categoriesDeferred.await()
                val newArrivals = newArrivalsDeferred.await()
                val popularItems = popularItemsDeferred.await()

                // Update UI
                _categories.value = categories
                _newArrivals.value = newArrivals
                _popularItems.value = popularItems

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load home data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            try {
                if (wishlistRepository.isProductInWishlist(product.id)) {
                    wishlistRepository.removeFromWishlist(product.id)
                } else {
                    wishlistRepository.addToWishlist(product.id)
                }
                // Refresh products to update wishlist status
                refreshProducts()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update wishlist"
            }
        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            try {
                val newArrivals = productRepository.getNewArrivals()
                val popularItems = productRepository.getPopularItems()
                
                _newArrivals.value = newArrivals
                _popularItems.value = popularItems
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to refresh products"
            }
        }
    }

    fun retryLoading() {
        loadHomeData()
    }
}
