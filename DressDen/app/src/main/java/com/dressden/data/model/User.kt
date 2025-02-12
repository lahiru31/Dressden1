package com.dressden.data.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val address: Address? = null
)

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
