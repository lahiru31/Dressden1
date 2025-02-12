package com.dressden.di

import androidx.lifecycle.SavedStateHandle
import com.dressden.data.repository.CategoryRepository
import com.dressden.data.repository.ProductRepository
import com.dressden.data.repository.UserRepository
import com.dressden.data.repository.WishlistRepository
import com.dressden.ui.auth.AuthViewModel
import com.dressden.ui.cart.CartViewModel
import com.dressden.ui.catalog.CatalogViewModel
import com.dressden.ui.home.HomeViewModel
import com.dressden.ui.main.MainViewModel
import com.dressden.ui.product.ProductDetailViewModel
import com.dressden.ui.profile.ProfileViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideMainViewModel(
        userRepository: UserRepository
    ): MainViewModel {
        return MainViewModel(userRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideHomeViewModel(
        categoryRepository: CategoryRepository,
        productRepository: ProductRepository,
        wishlistRepository: WishlistRepository
    ): HomeViewModel {
        return HomeViewModel(categoryRepository, productRepository, wishlistRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCatalogViewModel(
        categoryRepository: CategoryRepository,
        productRepository: ProductRepository,
        wishlistRepository: WishlistRepository
    ): CatalogViewModel {
        return CatalogViewModel(categoryRepository, productRepository, wishlistRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideProductDetailViewModel(
        savedStateHandle: SavedStateHandle,
        productRepository: ProductRepository,
        wishlistRepository: WishlistRepository
    ): ProductDetailViewModel {
        return ProductDetailViewModel(savedStateHandle, productRepository, wishlistRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCartViewModel(
        productRepository: ProductRepository
    ): CartViewModel {
        return CartViewModel(productRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideProfileViewModel(
        userRepository: UserRepository,
        wishlistRepository: WishlistRepository
    ): ProfileViewModel {
        return ProfileViewModel(userRepository, wishlistRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideAuthViewModel(
        userRepository: UserRepository
    ): AuthViewModel {
        return AuthViewModel(userRepository)
    }
}
