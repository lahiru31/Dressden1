package com.dressden.data.remote

import com.dressden.data.model.User
import com.dressden.data.model.Product
import retrofit2.http.*

interface ApiService {
    // User endpoints
    @GET("users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): User

    @POST("users")
    suspend fun createUserProfile(@Body user: User): User

    @PUT("users/{userId}")
    suspend fun updateUserProfile(@Body user: User): User

    // Product endpoints
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<Product>

    @GET("products/{productId}")
    suspend fun getProduct(@Path("productId") productId: String): Product

    @GET("products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<Product>

    // Cart endpoints
    @POST("cart/add")
    suspend fun addToCart(
        @Query("productId") productId: String,
        @Query("quantity") quantity: Int
    ): CartResponse

    @DELETE("cart/remove/{productId}")
    suspend fun removeFromCart(@Path("productId") productId: String): CartResponse

    @GET("cart")
    suspend fun getCart(): CartResponse

    // Order endpoints
    @POST("orders")
    suspend fun createOrder(@Body order: OrderRequest): OrderResponse

    @GET("orders")
    suspend fun getOrders(): List<OrderResponse>

    @GET("orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): OrderResponse
}

data class CartResponse(
    val items: List<CartItem>,
    val totalAmount: Double,
    val totalItems: Int
)

data class CartItem(
    val product: Product,
    val quantity: Int,
    val price: Double
)

data class OrderRequest(
    val items: List<CartItem>,
    val shippingAddress: Address,
    val paymentMethod: String
)

data class OrderResponse(
    val orderId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val shippingAddress: Address,
    val paymentStatus: String
)
